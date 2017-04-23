/*
 *     Minero is a minimal budget application
 *     Copyright (C) 2017 Jacob Katz
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.jrkatz.minero.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.google.common.io.Resources;

/**
 * @Author jrkatz
 * @Date 2/20/2017.
 */

class BudgetDbHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 2;
    private static final String DB_NAME = "minero";

    enum Table {
        DEBIT(1, "CREATE TABLE debit(" +
                "id INTEGER PRIMARY KEY ASC," +
                "budget_id INTEGER REFERENCES budget(id) ON DELETE CASCADE," +
                "budget_period_id INTEGER REFERENCES budget_period(id) ON DELETE CASCADE," +
                "amount INTEGER," +
                "description TEXT," +
                "time INTEGER," +
                "zone TEXT" +
                ");"),
        DEBIT_TIME_IDX(1, "CREATE INDEX idx_debit_time ON debit (time)"),
        DEBIT_PERIOD_IDX(1, "CREATE INDEX idx_debit_period ON debit(budget_period_id)"),
        DEBIT_BUDGET_IDX(1, "CREATE INDEX idx_debit_budget ON debit(budget_id)"),
        BUDGET(1, "CREATE TABLE budget(" +
                "id INTEGER PRIMARY KEY ASC," +
                "distribution INTEGER," +
                "name TEXT," +
                "period_definition TEXT," +
                "running_total INTEGER" +
                ");"),
        BUDGET_PERIOD(1, "CREATE TABLE budget_period(" +
                "id INTEGER PRIMARY KEY ASC," +
                "budget_id INTEGER REFERENCES budget(id) ON DELETE CASCADE," +
                "distribution TEXT," +
                "start TEXT," +
                "end TEXT" +
                ");"),
        BUDGET_PERIOD_IDX(1, "CREATE INDEX idx_budget_period_time ON budget_period(start, end);")
        //TODO consider adding a constraint preventing overlapping periods?
        ;
        private final String mSql;
        private final int mFirstVersion;
        Table(final int firstVersion, @NonNull final String sql) {
            mFirstVersion = firstVersion;
            mSql = sql;
        }

        @NonNull
        String getSql() {
            return mSql;
        }

        int getFirstVersion() {
            return mFirstVersion;
        }
    }

    public BudgetDbHelper(@NonNull final Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(@NonNull final SQLiteDatabase db) {
        for (final Table  t : Table.values()) {
            db.execSQL(t.getSql());
        }
    }

    private void upgrade(@NonNull final SQLiteDatabase db, int newVersion) {
        //add missing tables
        for (final Table t : Table.values()) {
            if (newVersion == t.getFirstVersion()) {
                db.execSQL(t.getSql());
            }
        }

        switch(newVersion) {
            case 2:
                db.execSQL(BudgetSql.BUDGET_UPDATE_2);
                //OK, now that's done, calculate the running total for all budgets.
                break;
        }
    }

    @Override
    public void onUpgrade(@NonNull final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        db.beginTransaction();
        for (int version = oldVersion + 1; version <= newVersion; version++) {
            upgrade(db, version);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }
}