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

import net.jrkatz.minero.data.Debit;

import java.util.ArrayList;
import java.util.List;

/**
 * A ScrollView subclass for displaying lists of Debit actions
 */
public class DebitListView extends ListView {
    private ArrayList<Debit> mDebits;
    private DebitsAdapter mAdapter;
    private ConfirmDebitRemoval mDebitRemoval;

    public DebitListView(Context context, AttributeSet attrs) {
        super(context, attrs, R.attr.debitListViewStyle);
    }

    public interface ConfirmDebitRemoval {
        void confirmDebitRemoval(@NonNull final Debit debit);
    }

    public void bind(final @NonNull ArrayList<Debit> debits, @Nullable final ConfirmDebitRemoval debitRemoval) {
        mDebits = debits;
        mAdapter = new DebitsAdapter(getContext(), mDebits);
        mDebitRemoval = debitRemoval;
        updateView();
    }

    public void updateView() {
        this.setAdapter(mAdapter);
    }

    private class DebitsAdapter extends ArrayAdapter<Debit> {
        public DebitsAdapter(Context context, List<Debit> debits) {
            super(context, 0, debits);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            final Debit debit = getItem(position);
            if (view == null) {
                view = new DebitView(getContext(), null);
            }

            ((DebitView) view).bind(debit);

            view.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mDebitRemoval == null) {
                        return false;
                    }
                    mDebitRemoval.confirmDebitRemoval(debit);
                    return true;
                }
            });
            return view;
        }
    }
}
