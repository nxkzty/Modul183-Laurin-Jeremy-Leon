ALTER TABLE team
    ADD team_leader_id UUID;

ALTER TABLE team
    ADD CONSTRAINT FK_TEAM_ON_TEAM_LEADER FOREIGN KEY (team_leader_id) REFERENCES user_entity (id);