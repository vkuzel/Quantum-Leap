UPDATE core.table_preferences
SET enabled_columns = ARRAY ['id', 'role']
WHERE entity_identifier = 'core.person_role';
