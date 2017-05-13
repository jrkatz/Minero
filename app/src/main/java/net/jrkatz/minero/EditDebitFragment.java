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
import android.support.annotation.NonNull;

import net.jrkatz.minero.data.DataContextFactory;
import net.jrkatz.minero.data.DebitConsumer;
import net.jrkatz.minero.data.IDataChangeListener;
import net.jrkatz.minero.data.IDataContext;
import net.jrkatz.minero.data.ProviderException;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditDebitFragment extends DialogFragment {
    IDataChangeListener mListener;

    public static EditDebitFragment newInstance(@NonNull final DebitListEntry entry) {
        Bundle args = new Bundle();
        args.putParcelable("entry", entry);
        EditDebitFragment fragment = new EditDebitFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final DebitListEntry entry = getArguments().getParcelable("entry");
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final DebitEntryView debitEntryView = new DebitEntryView(getContext(), null);
        debitEntryView.bind(new DebitConsumer(mListener) {
            @Override
            protected boolean _consume(int amount, String description) {
                try(final IDataContext dataContext = DataContextFactory.getDataContext(getContext())) {
                    dataContext.getDebitProvider().amendDebit(dataContext, entry.getLastId(), amount, description);
                    dataContext.markSuccessful();
                } catch (ProviderException e) {
                    //TODO handle better
                    throw new RuntimeException(e);
                }
                getDialog().dismiss();
                return true;
            }
        }, entry);

        builder.setMessage("Edit debit entry?")
                .setView(debitEntryView)
                .setNeutralButton(getResources().getText(R.string.delete),new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try (final IDataContext dataContext = DataContextFactory.getDataContext(getContext())) {
                            dataContext.getDebitProvider().amendDebit(dataContext, entry.getLastId(), 0);
                            dataContext.markSuccessful();
                        } catch (ProviderException e) {
                            //TODO handle better
                            throw new RuntimeException(e);
                        }
                        if (mListener != null) {
                            mListener.dataChanged();
                        }
                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        debitEntryView.finishEntry(); //If for whatever reason this doesn't work
                        //(like the amount field is blank, etc), that's OK. They must have decided
                        //against changes.
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
