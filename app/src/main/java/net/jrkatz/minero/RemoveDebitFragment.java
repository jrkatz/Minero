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


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import net.jrkatz.minero.data.DataContextFactory;
import net.jrkatz.minero.data.Debit;
import net.jrkatz.minero.data.IDataChangeListener;
import net.jrkatz.minero.data.IDataContext;
import net.jrkatz.minero.data.ProviderException;


/**
 * A simple {@link Fragment} subclass.
 */
public class RemoveDebitFragment extends DialogFragment {
    IDataChangeListener mListener;

    public static RemoveDebitFragment newInstance(final Debit debit) {
        Bundle args = new Bundle();
        args.putParcelable("debit", debit);
        RemoveDebitFragment fragment = new RemoveDebitFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Debit debit = getArguments().getParcelable("debit");
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        DebitView debitView = new DebitView(getContext(), null);
        debitView.bind(debit);
        builder.setMessage("Delete debit entry?")
                .setView(debitView)
                .setPositiveButton(getResources().getText(R.string.delete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try(final IDataContext dataContext = DataContextFactory.getDataContext(getContext())) {
                            dataContext.getDebitProvider().removeDebit(dataContext, debit.getId());
                            dataContext.markSuccessful();
                        } catch (ProviderException e) {
                            //TODO handle better
                            throw new RuntimeException(e);
                        }
                        if (mListener != null) {
                            mListener.rerender();
                        }
                    }
                })
                .setNegativeButton(getResources().getText(
                        android.R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onCancel(dialog);
                    }
                })
                .setCancelable(true);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (IDataChangeListener) context;
        } catch (ClassCastException e) {
            mListener = null;
        }
    }
}
