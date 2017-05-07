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

package net.jrkatz.minero;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import net.jrkatz.minero.data.Debit;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

/**
 * @Author jrkatz
 * @Date 5/6/2017.
 */

public class DebitListEntry implements Parcelable {
    @NonNull private final ImmutableList<Debit> mDebits;
    private final int mAmount;
    private final String mDescription;
    @NonNull private final DateTime mTime;
    @NonNull private final Debit mLastDebit;

    private DebitListEntry(@NonNull final ImmutableList<Debit> debits) throws IllegalArgumentException {
        if (debits.isEmpty()) {
            throw new IllegalArgumentException("debit list must not be empty");
        }
        mDebits = debits;
        mLastDebit = debits.get(debits.size() - 1);
        int amt = 0;
        for (final Debit d : mDebits) {
            amt += d.getAmount();
        }
        mAmount = amt;
        mDescription = mLastDebit.getDescription();
        mTime = debits.get(0).getTime();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(mDebits);
    }

    protected DebitListEntry(Parcel in) {
        this(ImmutableList.copyOf(in.readArrayList(Debit.class.getClassLoader())));
    }
    public static final Creator<DebitListEntry> CREATOR = new Creator<DebitListEntry>() {
        @Override
        public DebitListEntry createFromParcel(Parcel in) {
            return new DebitListEntry(in);
        }

        @Override
        public DebitListEntry[] newArray(int size) {
            return new DebitListEntry[size];
        }
    };

    public ImmutableList<Debit> getDebits() {
        return mDebits;
    }

    public String getDescription() {
        return mDescription;
    }

    public int getAmount() {
        return mAmount;
    }

    @NonNull
    public DateTime getTime() {
        return mTime;
    }

    public long getLastId() {
        return mLastDebit.getId();
    }

    public static ImmutableList<DebitListEntry> makeEntries(@NonNull final Collection<Debit> debits) {
        final ImmutableList.Builder<DebitListEntry> entries = ImmutableList.builder();
        final HashMap<Long, Debit> children = new HashMap<>();
        final ArrayList<Debit> parents = new ArrayList<>();
        for (final Debit d : debits) {
            if (d.getParentId() == null) {
                parents.add(d);
            } else {
                children.put(d.getParentId(), d);
            }
        }
        Collections.sort(parents);
        for (Debit parent : parents) {
            final ImmutableList.Builder<Debit> entry = ImmutableList.builder();
            entry.add(parent);
            while (children.containsKey(parent.getId())) {
                parent = children.get(parent.getId());
                entry.add(parent);
            }
            entries.add(new DebitListEntry(entry.build()));
        }
        return entries.build();
    }

    @Override
    public int describeContents() {
        return 0;
    }
}