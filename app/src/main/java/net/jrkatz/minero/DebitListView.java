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

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.collect.ImmutableList;

import net.jrkatz.minero.data.Debit;

import java.util.ArrayList;
import java.util.List;

/**
 * A ScrollView subclass for displaying lists of Debit actions
 */
public class DebitListView extends ListView {
    private ImmutableList<DebitListEntry> mEntries;
    private DebitsAdapter mAdapter;
    private ConfirmDebitRemoval mDebitRemoval;

    public DebitListView(Context context, AttributeSet attrs) {
        super(context, attrs, R.attr.debitListViewStyle);
    }

    public interface ConfirmDebitRemoval {
        void confirmDebitRemoval(@NonNull final DebitListEntry entry);
    }

    public void bind(final @NonNull ArrayList<Debit> debits, @Nullable final ConfirmDebitRemoval debitRemoval) {
        mEntries = DebitListEntry.makeEntries(debits);
        mAdapter = new DebitsAdapter(getContext(), mEntries);
        mDebitRemoval = debitRemoval;
        updateView();
    }

    public void updateView() {
        this.setAdapter(mAdapter);
    }

    private class DebitsAdapter extends ArrayAdapter<DebitListEntry> {
        public DebitsAdapter(Context context, List<DebitListEntry> debits) {
            super(context, 0, debits);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            final DebitListEntry entry = getItem(position);
            if (view == null) {
                view = new DebitListEntryView(getContext(), null);
            }

            ((DebitListEntryView) view).bind(entry);

            view.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mDebitRemoval == null) {
                        return false;
                    }
                    mDebitRemoval.confirmDebitRemoval(entry);
                    return true;
                }
            });
            return view;
        }
    }
}
