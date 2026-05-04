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
    team_id UUID NOT NULL,
    task_id UUID NOT NULL,
    CONSTRAINT pk_team_tasks PRIMARY KEY (team_id, task_id)
);

CREATE TABLE user_entity
(
    id            UUID NOT NULL,
    updated_at    TIMESTAMP WITHOUT TIME ZONE,
    created_at    TIMESTAMP WITHOUT TIME ZONE,
    name          VARCHAR(255),
    password_hash VARCHAR(255),
    role          VARCHAR(255),
    CONSTRAINT pk_user_entity PRIMARY KEY (id)
);

CREATE TABLE user_entity_teams
(
    user_entity_id UUID NOT NULL,
    team_id        UUID NOT NULL,
    CONSTRAINT pk_user_entity_teams PRIMARY KEY (user_entity_id, team_id)
);

ALTER TABLE team_tasks
    ADD CONSTRAINT uc_team_tasks_task UNIQUE (task_id);

ALTER TABLE task
    ADD CONSTRAINT fk_task_on_assignee FOREIGN KEY (assignee_id) REFERENCES user_entity (id);

ALTER TABLE task
    ADD CONSTRAINT fk_task_on_issuer FOREIGN KEY (issuer_id) REFERENCES user_entity (id);

ALTER TABLE team_tasks
    ADD CONSTRAINT fk_team_tasks_on_task FOREIGN KEY (task_id) REFERENCES task (id);

ALTER TABLE team_tasks
    ADD CONSTRAINT fk_team_tasks_on_team FOREIGN KEY (team_id) REFERENCES team (id);

ALTER TABLE user_entity_teams
    ADD CONSTRAINT fk_user_entity_teams_on_team FOREIGN KEY (team_id) REFERENCES team (id);

ALTER TABLE user_entity_teams
    ADD CONSTRAINT fk_user_entity_teams_on_user_entity FOREIGN KEY (user_entity_id) REFERENCES user_entity (id);
