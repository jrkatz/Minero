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
import android.support.annotation.NonNull;

/**
 * @Author jrkatz
 * @Date 4/16/2017.
 */

public class DbDataContext implements IDataContext<DbDataContext> {
    final SQLiteDatabase mDb;

    public DbDataContext(final Context context) {
        mDb = new BudgetDbHelper(context).getWritableDatabase();
        mDb.beginTransaction();
    }

    public SQLiteDatabase getDb() {
        return mDb;
    }

    @Override
    public void markSuccessful() {
        mDb.setTransactionSuccessful();
    }

    @Override
    public void close() {
        if (mDb != null) {
            mDb.endTransaction();
            mDb.close();
        }
    }

    @Override
    @NonNull
    public AbstractBudgetProvider<DbDataContext> getBudgetProvider() {
        return new BudgetProvider();
    }

    @Override
    @NonNull
    public AbstractDebitProvider<DbDataContext> getDebitProvider() {
        return new DebitProvider();
    }

    @Override
    @NonNull
    public AbstractBudgetPeriodProvider<DbDataContext> getBudgetPeriodProvider() {
        return new BudgetPeriodProvider();
    }
}
