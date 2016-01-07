package de.paulwein.paul.database;

public class DatabaseTables {

	public interface AlarmColumns {

		public static final String TABLE_NAME = "alarm_tbl";

		public static final int ALARM_TYPE_ALARM_CLOCK = 1;
		public static final int ALARM_TYPE_REMINDER = 2;

		final String ID = "_id";
		final String ALARM_SUBJECT = "alarm_subject";
		final String ALARM_TIME = "alarm_time";
		final String ALARM_TYPE = "alarm_type";
		final String ENABLED = "enabled";

		final String[] ALL_COLUMNS = { ID, ALARM_SUBJECT, ALARM_TIME,
				ALARM_TYPE, ENABLED, };
	}

	public class AlarmTbl implements AlarmColumns {

		public static final String CREATE_ALARM_TBL = "CREATE TABLE "
				+ TABLE_NAME + " (" + ID + " INTEGER PRIMARY KEY NOT NULL, "
				+ ALARM_SUBJECT + " TEXT NOT NULL, " + ALARM_TIME
				+ " INTEGER NOT NULL, " + ALARM_TYPE + " INTEGER NOT NULL, "
				+ ENABLED + " INTEGER);";

		public static final String DROP_ALARM_TBL = "DROP TABLE IF EXISTS "
				+ TABLE_NAME;
	}

	public interface NotesColumns {

		public static final String TABLE_NAME = "notes_tbl";

		final String ID = "_id";
		final String NOTE_SUBJECT = "note_subject";
		final String NOTE_TEXT = "note_text";
		final String CREATED_TIMESTAMP = "created_timestamp";
		final String LIST_ID = "list_id";

		final String[] ALL_COLUMNS = { TABLE_NAME + "." + ID,
				TABLE_NAME + "." + NOTE_SUBJECT, TABLE_NAME + "." + NOTE_TEXT,
				TABLE_NAME + "." + CREATED_TIMESTAMP,
				TABLE_NAME + "." + LIST_ID };
	}

	public class NotesTbl implements NotesColumns {

		public static final String CREATE_NOTES_TBL = "CREATE TABLE "
				+ TABLE_NAME + " (" + ID + " INTEGER PRIMARY KEY NOT NULL, "
				+ NOTE_SUBJECT + " TEXT NOT NULL, " + NOTE_TEXT
				+ " TEXT NOT NULL, " + CREATED_TIMESTAMP + " INTEGER NOT NULL,"
				+ LIST_ID + " INTEGER NOT NULL," + "FOREIGN KEY(" + LIST_ID
				+ ") REFERENCES " + NotesListColumns.TABLE_NAME + "("
				+ NotesListColumns.ID + "));";

		public static final String DROP_NOTES_TBL = "DROP TABLE IF EXISTS "
				+ TABLE_NAME;
	}

	public interface NotesListColumns {

		public static final String TABLE_NAME = "list_tbl";

		final String ID = "_id";
		final String LIST_NAME = "list_name";

		final String[] ALL_COLUMNS = { ID, LIST_NAME };
	}

	public class NotesListTbl implements NotesListColumns {

		public static final String CREATE_LIST_TBL = "CREATE TABLE "
				+ TABLE_NAME + " (" + ID + " INTEGER PRIMARY KEY NOT NULL, "
				+ LIST_NAME + " TEXT NOT NULL);";

		public static final String DROP_LIST_TBL = "DROP TABLE IF EXISTS "
				+ TABLE_NAME;
	}

	public interface TasksColumns {

		public static final String TABLE_NAME = "tasks_tbl";

		final String ID = "_id";
		final String TASK_SUBJECT = "note_subject";
		final String TASK_TEXT = "note_text";
		final String CREATED_TIMESTAMP = "created_timestamp";
		final String LIST_ID = "list_id";
		final String PRIORITY = "priority";
		final String DONE = "done";
		final String DONE_TIME = "done_time";
		final String SETTLEMENT_DATE = "settlement_date";
		final String REMINDER = "reminder";

		final String[] ALL_COLUMNS = { TABLE_NAME + "." + ID,
				TABLE_NAME + "." + TASK_SUBJECT, TABLE_NAME + "." + TASK_TEXT,
				TABLE_NAME + "." + CREATED_TIMESTAMP,
				TABLE_NAME + "." + LIST_ID, TABLE_NAME + "." + PRIORITY,
				TABLE_NAME + "." + DONE, TABLE_NAME + "." + DONE_TIME,
				TABLE_NAME + "." + SETTLEMENT_DATE,
				TABLE_NAME + "." + REMINDER, };
	}

	public class TasksTbl implements TasksColumns {

		public static final String CREATE_TASKS_TBL = "CREATE TABLE "
				+ TABLE_NAME + " (" + ID + " INTEGER PRIMARY KEY NOT NULL, "
				+ TASK_SUBJECT + " TEXT NOT NULL, " + TASK_TEXT
				+ " TEXT NOT NULL, " + CREATED_TIMESTAMP + " INTEGER NOT NULL,"
				+ PRIORITY + " INTEGER NOT NULL," + DONE + " INTEGER,"
				+ DONE_TIME + " INTEGER," + SETTLEMENT_DATE + " INTEGER,"
				+ REMINDER + " INTEGER," + LIST_ID + " INTEGER NOT NULL,"
				+ "FOREIGN KEY(" + LIST_ID + ") REFERENCES "
				+ NotesListColumns.TABLE_NAME + "(" + NotesListColumns.ID
				+ "));";

		public static final String DROP_TASKS_TBL = "DROP TABLE IF EXISTS "
				+ TABLE_NAME;
	}
	
	public interface LocationsColumns{
		final String TABLE_NAME = "locations_tbl";
		
		final String ID = "_id";
		final String LOCATION_NAME = "location_name";
		final String LOCATION_TYPE = "location_type";
		final String LOCATION_IDENTIFIER = "location_identifier";
		
	}
	
	public class LocationsTbl implements LocationsColumns {

		public static final String CREATE_LOCATIONS_TBL = "CREATE TABLE "
				+ TABLE_NAME + " (" + ID + " INTEGER PRIMARY KEY NOT NULL, "
				+ LOCATION_NAME + " TEXT NOT NULL, " + LOCATION_TYPE
				+ " INTEGER NOT NULL, " + LOCATION_IDENTIFIER + " TEXT NOT NULL);";

		public static final String DROP_LOCATIONS_TBL = "DROP TABLE IF EXISTS "
				+ TABLE_NAME;
	}
	
	public interface LocationPropertiesColumns{
		final String TABLE_NAME = "location_properties_tbl";

		final String LOCATION_ID = "location_id";
		final String LOCATION_PROPERTY_TYPE = "location_property_type";
		final String LOCATION_PROPERTY = "location_property";
		
	}
	
	public class LocationPropertiesTbl implements LocationPropertiesColumns {

		public static final String CREATE_LOCATION_PROPERTIES_TBL = "CREATE TABLE "
				+ TABLE_NAME + " (" + LOCATION_ID + " INTEGER NOT NULL, "
				+ LOCATION_PROPERTY_TYPE + " INTEGER NOT NULL, " + LOCATION_PROPERTY
				+ " INTEGER NOT NULL,"
				+ "FOREIGN KEY(" + LOCATION_ID + ") REFERENCES "
				+ LocationsColumns.TABLE_NAME + "(" + LocationsColumns.ID
				+ "));";

		public static final String DROP_LOCATION_PROPERTIES_TBL = "DROP TABLE IF EXISTS "
				+ TABLE_NAME;
	}
}
