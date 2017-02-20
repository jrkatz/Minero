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
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.jrkatz.minero.budget.BudgetPeriod;

import org.joda.time.LocalDate;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BudgetPeriodFrag.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BudgetPeriodFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BudgetPeriodFrag extends Fragment {
    private static final String ARG_BUDGET_PERIOD = "budgetPeriod";

    private BudgetPeriod mBudgetPeriod;

    private OnFragmentInteractionListener mListener;

    public BudgetPeriodFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment
     *
     * @param budgetPeriod The budget period to display
     * @return A new instance of fragment BudgetPeriodFrag.
     */
    public static BudgetPeriodFrag newInstance(BudgetPeriod budgetPeriod) {
        BudgetPeriodFrag fragment = new BudgetPeriodFrag();
        Bundle args = new Bundle();
        args.putParcelable(ARG_BUDGET_PERIOD, budgetPeriod);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mBudgetPeriod = getArguments().getParcelable(ARG_BUDGET_PERIOD);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_budget_period, container, false);
        final TextView remainingAmt = (TextView)v.findViewById(R.id.remainingAmt);
        remainingAmt.setText(Long.toString(mBudgetPeriod.getRemaining()));

        final TextView periodView = (TextView)v.findViewById(R.id.period);
        final LocalDate end = mBudgetPeriod.getPeriod().getEnd();
        final String untilDateString = String.format(getResources().getString(R.string.until_date_format), end.toString(getResources().getString(R.string.ymd_format)));
        periodView.setText(untilDateString);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
