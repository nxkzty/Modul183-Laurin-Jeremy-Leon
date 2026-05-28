package ch.taskify.view.myTaskify.team

import ch.taskify.dto.TeamDTO
import ch.taskify.dto.UserDTO
import ch.taskify.entity.user.Role
import ch.taskify.service.team.TeamService
import ch.taskify.service.user.UserService
import ch.taskify.utils.CurrentUser
import ch.taskify.utils.notify.Notify
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.avatar.Avatar
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.html.H2
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import jakarta.annotation.security.PermitAll
import java.util.*

@Route("myTaskify/team")
@PageTitle("Mein Team")
@PermitAll
class Team(
    private val teamService: TeamService,
    private val userService: UserService
) : VerticalLayout() {

    private val activeTeamSelect = ComboBox<TeamDTO>()
    private val teamOverview = Div()

    private var visibleTeams: List<TeamDTO> = emptyList()
    private var allTeams: List<TeamDTO> = emptyList()
    private var users: List<UserDTO> = emptyList()
    private var headerActions = HorizontalLayout()

    private val isAdmin: Boolean
        get() = CurrentUser.principalAsUserEntity?.role == Role.ADMIN

    init {
        setSizeFull()
        isPadding = false
        isSpacing = false

        users = userService.findAll().sortedBy { it.name.lowercase() }

        add(buildHeader(), buildOverview())
        configureActiveTeamSelect()
        refreshPage()
    }

    private fun buildHeader(): HorizontalLayout {
        val title = H1("Team").apply {
            style
                .set("margin", "0")
                .set("font-size", "clamp(22px, 4vw, 32px)")
                .set("font-weight", "700")
                .set("color", "var(--lumo-header-text-color)")
        }

        val subtitle = Span("Deine Teams und Mitglieder auf einen Blick").apply {
            style
                .set("color", "var(--lumo-secondary-text-color)")
                .set("font-size", "14px")
        }

        activeTeamSelect.apply {
            label = "Aktives Team"
            width = "260px"
            placeholder = "Team auswählen"
            setItemLabelGenerator { it.name }
            addValueChangeListener {
                refreshOverview()
                refreshHeaderActions()
            }
        }

        val textBlock = VerticalLayout(title, subtitle, activeTeamSelect).apply {
            isPadding = false
            isSpacing = false
            style.set("gap", "8px")
        }

        headerActions = HorizontalLayout().apply {
            defaultVerticalComponentAlignment = FlexComponent.Alignment.CENTER
            isSpacing = true
        }

        val header = HorizontalLayout(textBlock, headerActions).apply {
            setWidthFull()
            justifyContentMode = FlexComponent.JustifyContentMode.BETWEEN
            defaultVerticalComponentAlignment = FlexComponent.Alignment.CENTER
            style
                .set("padding", "clamp(16px, 3vw, 28px) clamp(16px, 3vw, 32px) 20px")
                .set("box-sizing", "border-box")
                .set("gap", "16px")
                .set("flex-wrap", "wrap")
        }

        refreshHeaderActions()
        return header
    }

    private fun refreshHeaderActions() {
        headerActions.removeAll()

        if (isAdmin) {
            val createTeam = Button("Team erstellen", Icon(VaadinIcon.PLUS)) {
                TeamCreateDialog(
                    onCreate = { name, teamLeader ,description -> createTeam(name, teamLeader ,description) },
                    users = users
                ).open()
            }.apply {
                addThemeVariants(ButtonVariant.LUMO_PRIMARY)
            }
            headerActions.add(createTeam)
        }

        if (canManageMembers(activeTeamSelect.value)) {
            val addMembers = Button("Mitglieder verwalten", Icon(VaadinIcon.USERS)) {
                val activeTeam = activeTeamSelect.value
                if (activeTeam?.id == null) {
                    Notify.warning("Bitte zuerst ein aktives Team auswählen.")
                    return@Button
                }

                TeamMembersDialog(
                    team = activeTeam,
                    users = users,
                    selectedMembers = teamService.findMembers(activeTeam.id),
                    onSave = { userIds -> saveMembers(activeTeam, userIds) }
                ).open()
            }.apply {
                addThemeVariants(ButtonVariant.LUMO_PRIMARY)
            }
            headerActions.add(addMembers)
        }
    }

    private val currentUserId: UUID?
        get() = CurrentUser.principalAsUserEntity?.id

    private fun canManageMembers(team: TeamDTO?): Boolean {
        if (team == null) return false
        if (isAdmin) return true
        return team.teamLeaderId == currentUserId
    }

    private fun buildOverview(): VerticalLayout {
        val title = if (isAdmin) {
            H2("Admin Teamübersicht")
        } else {
            H2("Teamübersicht")
        }.apply {
            style
                .set("margin", "0")
                .set("font-size", "20px")
                .set("font-weight", "700")
        }

        val subtitle = if (isAdmin) {
            Span("Du siehst alle existierenden Teams")
        } else {
            Span("Alle Teams, in denen du Mitglied bist.")
        }.apply {
            style
                .set("color", "var(--lumo-secondary-text-color)")
                .set("font-size", "14px")
        }

        teamOverview.style
            .set("display", "grid")
            .set("grid-template-columns", "repeat(auto-fit, minmax(280px, 1fr))")
            .set("gap", "16px")
            .set("width", "100%")

        return VerticalLayout(title, subtitle, teamOverview).apply {
            setWidthFull()
            isPadding = false
            isSpacing = false
            style
                .set("gap", "12px")
                .set("padding", "0 clamp(16px, 3vw, 32px) 32px")
                .set("box-sizing", "border-box")
        }
    }

    private fun configureActiveTeamSelect() {
        activeTeamSelect.addValueChangeListener { refreshOverview() }
    }

    private fun refreshPage(selectedTeamId: UUID? = activeTeamSelect.value?.id) {
        allTeams = teamService.findAll()

        visibleTeams = if (isAdmin) {
            allTeams
        } else {
            val userId = CurrentUser.principalAsUserEntity?.id

            userId?.let {
                val memberTeams = teamService.findByUserId(it)
                val leaderTeams = teamService.findByLeaderId(it)

                (memberTeams + leaderTeams).distinctBy { team -> team.id }
            }.orEmpty()
        }

        val selectableTeams = if (isAdmin) allTeams else visibleTeams
        activeTeamSelect.setItems(selectableTeams)

        val selectedTeam = selectableTeams.find { it.id == selectedTeamId }
            ?: selectableTeams.firstOrNull()

        activeTeamSelect.value = selectedTeam
        refreshOverview()
    }

    private fun refreshOverview() {
        teamOverview.removeAll()

        if (visibleTeams.isEmpty()) {
            teamOverview.add(emptyState())
            return
        }

        visibleTeams.forEach { team ->
            teamOverview.add(teamCard(team))
        }
    }

    private fun createTeam(name: String, teamLeader: UserDTO ,description: String?) {
        try {
            val createdTeam = teamService.createTeam(name, teamLeader, description)
            refreshPage(createdTeam.id)
            Notify.success("Team erstellt.")
        } catch (exception: IllegalArgumentException) {
            Notify.error("Team konnte nicht erstellt werden.")
        }
    }

    private fun saveMembers(team: TeamDTO, userIds: Set<UUID>) {
        val teamId = team.id ?: return
        val idsWithLeader: Set<UUID>
        teamService.updateMembers(teamId, userIds)
        refreshPage(teamId)
        Notify.success("Mitglieder gespeichert.")
    }


    private fun teamCard(team: TeamDTO): Component {
        val members = team.id?.let { teamService.findMembers(it) }.orEmpty()
        val isActive = activeTeamSelect.value?.id == team.id

        val name = Span(team.name).apply {
            style
                .set("font-weight", "700")
                .set("font-size", "18px")
        }

        val description = Span(team.description?.takeIf { it.isNotBlank() } ?: "Keine Beschreibung").apply {
            style
                .set("color", "var(--lumo-secondary-text-color)")
                .set("font-size", "14px")
                .set("line-height", "1.4")
        }

        val count = Span("${members.size} Mitglieder").apply {
            style
                .set("background", "var(--lumo-primary-color-10pct)")
                .set("color", "var(--lumo-primary-text-color)")
                .set("border-radius", "999px")
                .set("font-size", "12px")
                .set("font-weight", "700")
                .set("padding", "4px 10px")
        }

        val header = HorizontalLayout(name, count).apply {
            setWidthFull()
            justifyContentMode = FlexComponent.JustifyContentMode.BETWEEN
            defaultVerticalComponentAlignment = FlexComponent.Alignment.CENTER
            style.set("gap", "10px")
        }

        val memberList = VerticalLayout().apply {
            isPadding = false
            isSpacing = false
            style.set("gap", "8px")
        }

        if (members.isEmpty()) {
            memberList.add(Span("Noch keine Mitglieder.").apply {
                style
                    .set("color", "var(--lumo-tertiary-text-color)")
                    .set("font-size", "14px")
            })
        } else {
            members.forEach { member -> memberList.add(memberRow(member)) }
        }

        val leader = Span(
            "Teamleiter: ${team.teamLeaderName ?: "Nicht zugewiesen"}"
        ).apply {
            style
                .set("color", "var(--lumo-secondary-text-color)")
                .set("font-size", "13px")
        }

        return VerticalLayout(header, description, leader, memberList).apply {
            isPadding = true
            isSpacing = false
            style
                .set("gap", "14px")
                .set("background", "var(--lumo-base-color)")
                .set(
                    "border",
                    if (isActive) "1px solid var(--lumo-primary-color)" else "1px solid var(--lumo-contrast-10pct)"
                )
                .set(
                    "box-shadow",
                    if (isActive) "0 10px 28px var(--lumo-primary-color-10pct)" else "0 8px 24px var(--lumo-contrast-10pct)"
                )
                .set("border-radius", "14px")
                .set("box-sizing", "border-box")
                .set("min-height", "190px")
        }
    }

    private fun memberRow(user: UserDTO): HorizontalLayout {
        val avatar = Avatar(user.name).apply {
            width = "32px"
            height = "32px"
        }

        val name = Span(user.name).apply {
            style
                .set("font-weight", "600")
                .set("font-size", "14px")
        }

        val role = Span(user.role.displayName).apply {
            style
                .set("color", "var(--lumo-secondary-text-color)")
                .set("font-size", "12px")
        }

        return HorizontalLayout(
            avatar,
            VerticalLayout(name, role).apply {
                isPadding = false
                isSpacing = false
                style.set("gap", "0")
            }
        ).apply {
            defaultVerticalComponentAlignment = FlexComponent.Alignment.CENTER
            isPadding = false
            isSpacing = true
        }
    }

    private fun emptyState(): VerticalLayout {
        return VerticalLayout(
            Icon(VaadinIcon.USERS).apply {
                setSize("32px")
                style.set("color", "var(--lumo-tertiary-text-color)")
            },
            Span(if (isAdmin) "Noch keine Teams vorhanden." else "Du bist noch in keinem Team.")
        ).apply {
            setWidthFull()
            defaultHorizontalComponentAlignment = FlexComponent.Alignment.CENTER
            isPadding = true
            isSpacing = true
            style
                .set("background", "var(--lumo-base-color)")
                .set("border", "1px dashed var(--lumo-contrast-20pct)")
                .set("border-radius", "14px")
                .set("color", "var(--lumo-secondary-text-color)")
        }
    }
}
