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
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.jrkatz.minero.data.Debit;

/**
 * @Author jrkatz
 * @Date 4/16/2017.
 */

public class DebitView extends RelativeLayout {


    public DebitView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.view_debit, this);
    }

    public void bind(Debit debit) {
        final TextView amount = (TextView) findViewById(R.id.amount);
        final TextView description = (TextView) findViewById(R.id.description);
        final TextView time = (TextView) findViewById(R.id.time);

        final Resources r = getResources();
        final String amountStr = String.format(r.getString(R.string.currency_fmt),
                r.getString(R.string.currency_symbol),
                Long.toString(debit.getAmount()));

        amount.setText(amountStr);
        description.setText(debit.getDescription());
        time.setText(debit.getTime().toString());
    }


}
