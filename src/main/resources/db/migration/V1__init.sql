CREATE TABLE task
(
    id          UUID NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE,
    created_at  TIMESTAMP WITHOUT TIME ZONE,
    title       VARCHAR(255),
    state       VARCHAR(255),
    description VARCHAR(255),
    assignee_id UUID,
    issuer_id   UUID,
    risk        VARCHAR(255),
    CONSTRAINT pk_task PRIMARY KEY (id)
);

CREATE TABLE team
(
    id          UUID NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE,
    created_at  TIMESTAMP WITHOUT TIME ZONE,
    name        VARCHAR(255),
    description VARCHAR(255),
    CONSTRAINT pk_team PRIMARY KEY (id)
);

CREATE TABLE team_tasks
(
    team_id  UUID NOT NULL,
    tasks_id UUID NOT NULL,
    CONSTRAINT pk_team_tasks PRIMARY KEY (team_id, tasks_id)
);

CREATE TABLE "user"
(
    id            UUID NOT NULL,
    updated_at    TIMESTAMP WITHOUT TIME ZONE,
    created_at    TIMESTAMP WITHOUT TIME ZONE,
    name          VARCHAR(255),
    password_hash VARCHAR(255),
    role          VARCHAR(255),
    CONSTRAINT pk_user PRIMARY KEY (id)
);

CREATE TABLE user_teams
(
    user_id  UUID NOT NULL,
    teams_id UUID NOT NULL,
    CONSTRAINT pk_user_teams PRIMARY KEY (user_id, teams_id)
);

ALTER TABLE team_tasks
    ADD CONSTRAINT uc_team_tasks_tasks UNIQUE (tasks_id);

ALTER TABLE task
    ADD CONSTRAINT FK_TASK_ON_ASSIGNEE FOREIGN KEY (assignee_id) REFERENCES "user" (id);

ALTER TABLE task
    ADD CONSTRAINT FK_TASK_ON_ISSUER FOREIGN KEY (issuer_id) REFERENCES "user" (id);

ALTER TABLE team_tasks
    ADD CONSTRAINT fk_teatas_on_task FOREIGN KEY (tasks_id) REFERENCES task (id);

ALTER TABLE team_tasks
    ADD CONSTRAINT fk_teatas_on_team FOREIGN KEY (team_id) REFERENCES team (id);

ALTER TABLE user_teams
    ADD CONSTRAINT fk_usetea_on_team FOREIGN KEY (teams_id) REFERENCES team (id);

ALTER TABLE user_teams
    ADD CONSTRAINT fk_usetea_on_user FOREIGN KEY (user_id) REFERENCES "user" (id);