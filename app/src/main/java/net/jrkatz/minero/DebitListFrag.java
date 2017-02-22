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
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import net.jrkatz.minero.budget.Debit;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DebitListFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DebitListFrag extends Fragment {
    private static final String ARG_DEBITS = "debits";

    private ArrayList<Debit> mDebits;
    private DebitsAdapter mAdapter;

    public DebitListFrag() {
        // Required empty public constructor
    }

    public void updateDebits(ArrayList<Debit> debits) {
        mAdapter.clear();
        mAdapter.addAll(debits);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param debits Parameter 1.
     * @return A new instance of fragment DebitListFrag.
     */
    public static DebitListFrag newInstance(ArrayList<Debit> debits) {
        DebitListFrag fragment = new DebitListFrag();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_DEBITS, debits);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDebits = getArguments().getParcelableArrayList(ARG_DEBITS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_debit_list, container, false);
        ListView list = (ListView) view.findViewById(R.id.debit_list);
        mAdapter = new DebitsAdapter(getContext(), mDebits);
        list.setAdapter(mAdapter);
        return view;
    }

    private static class DebitsAdapter extends ArrayAdapter<Debit> {
        public DebitsAdapter(Context context, List<Debit> debits) {
            super(context, 0, debits);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            final Debit debit = getItem(position);
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.debit_list_item, parent, false);
            }
            final TextView amount = (TextView) view.findViewById(R.id.amount);
            final TextView description = (TextView) view.findViewById(R.id.description);
            final TextView time = (TextView) view.findViewById(R.id.time);

            amount.setText(Long.toString(debit.getAmount()));
            description.setText(debit.getDescription());
            time.setText(debit.getTime().toString());
            return view;
        }
    }
}
