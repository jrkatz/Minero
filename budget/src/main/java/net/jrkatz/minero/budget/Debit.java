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

package net.jrkatz.minero.budget;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.collect.ImmutableList;

import net.jrkatz.minero.budget.period.Period;

import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import java.util.Date;

/**
 * @Author jrkatz
 * @Date 2/19/2017.
 */
public class Debit implements Parcelable {
    private static final String[] COLUMNS = new String[]{"id", "amount", "description", "time"};
    private static final String TABLE_NAME = "debit";

    private final long mId;
    private final int mAmount;
    private final String mDescription;
    private final LocalDateTime mTime;

    private Debit(long id, int amount, String description, LocalDateTime time) {
        this.mId = id;
        this.mAmount = amount;
        this.mDescription = description;
        this.mTime = time;
    }

    public static final Creator<Debit> CREATOR = new Creator<Debit>() {
        @Override
        public Debit createFromParcel(Parcel in) {
            return new Debit(in);
        }

        @Override
        public Debit[] newArray(int size) {
            return new Debit[size];
        }
    };

    public long getAmount() {
        return mAmount;
    }

    public String getDescription() {
        return mDescription;
    }

    public LocalDateTime getTime() {
        return mTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeInt(mAmount);
        dest.writeString(mDescription);
        dest.writeLong(mTime.toDate().getTime());
    }
    private static Debit atCursor(final Cursor cursor) {
        return new Debit(cursor.getLong(0),
                cursor.getInt(1),
                cursor.getString(2),
                LocalDateTime.fromDateFields(new Date(cursor.getLong(3))));
    }

    private static Cursor debitQuery(SQLiteDatabase db, String where, String ... whereArgs) {
        return db.query(TABLE_NAME,
                COLUMNS,
                where,
                whereArgs,
                null, null, null);
    }
    public static ImmutableList<Debit> readDebits(final SQLiteDatabase db, final Period period) {
        final LocalTime startOfDay = new LocalTime(0,0);
        return readDebits(db,
                period.getStart().toLocalDateTime(startOfDay),
                period.getEnd().toLocalDateTime(startOfDay)
        );
    }

    public static ImmutableList<Debit> readDebits(final SQLiteDatabase db,
                                                  final LocalDateTime start,
                                                  final LocalDateTime end) {
        ImmutableList.Builder<Debit> debits = ImmutableList.builder();
        try(Cursor cursor = debitQuery(db,
                "time >= ? AND time < ?",
                Long.toString(start.toDate().getTime()),
                Long.toString(end.toDate().getTime())
        )) {
            while (cursor.moveToNext()) {
                debits.add(atCursor(cursor));
            }
        }
        return debits.build();
    }

    public static Debit createDebit(final SQLiteDatabase db,
                                    final int amount,
                                    final String description,
                                    final LocalDateTime time) throws SQLiteException {
        db.beginTransaction();
        ContentValues values = new ContentValues();
        values.put("amount", amount);
        values.put("description", description);
        values.put("time", (int) time.toDate().getTime());
        long id = db.insertOrThrow("debit", null, values);
        db.setTransactionSuccessful();
        db.endTransaction();
        return new Debit(id, amount, description, time);
    }

    protected Debit(Parcel in) {
        mId = in.readLong();
        mAmount = in.readInt();
        mDescription = in.readString();
        mTime = LocalDateTime.fromDateFields(new Date(in.readLong()));
    }
}