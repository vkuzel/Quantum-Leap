INSERT INTO core.table_preferences (database_table_name_with_schema, is_default, enabled_columns)
VALUES ('admin.notification', TRUE, ARRAY ['id', 'message', 'created_at', 'resolved_at']);
