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

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;

/**
 * @Author jrkatz
 * @Date 2/19/2017.
 */

public class BudgetPeriod implements Parcelable {
    private final long mId;
    private final long mBudgetId;
    private final Period mPeriod;
    private final long mDistribution;
    private final ImmutableList<Debit> mDebits;
    private final long mRemaining;

    public BudgetPeriod(
            final long id,
            final long budgetId,
            @NonNull final Period period,
            final long distribution,
            @NonNull final ImmutableList<Debit> debits
    ) {
        mId = id;
        mBudgetId = budgetId;
        mPeriod = period;
        mDistribution = distribution;
        mDebits = debits;
        long remaining = mDistribution;
        for(final Debit debit : mDebits) {
            remaining-=debit.getAmount();
        }

        mRemaining = remaining;
    }

    public long getBudgetId() {
        return mBudgetId;
    }

    public long getId() {
        return mId;
    }

    public long getRemaining() {
        return mRemaining;
    }

    public Period getPeriod() {
        return mPeriod;
    }

    public ImmutableCollection<Debit> getDebits() {
        return mDebits;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeLong(mBudgetId);
        dest.writeParcelable(mPeriod, flags);
        dest.writeLong(mDistribution);
        dest.writeTypedList(mDebits);
        dest.writeLong(mRemaining);
    }

    protected BudgetPeriod(Parcel in) {
        mId = in.readLong();
        mBudgetId = in.readLong();
        mPeriod = in.readParcelable(Period.class.getClassLoader());
        mDistribution = in.readLong();
        final ArrayList<Debit> debits = new ArrayList<>();
        in.readTypedList(debits, Debit.CREATOR);
        mDebits = ImmutableList.copyOf(debits);
        mRemaining = in.readLong();

    }

    public static final Creator<BudgetPeriod> CREATOR = new Creator<BudgetPeriod>() {
        @Override
        public BudgetPeriod createFromParcel(Parcel in) {
            return new BudgetPeriod(in);
        }

        @Override
        public BudgetPeriod[] newArray(int size) {
            return new BudgetPeriod[size];
        }
    };
}