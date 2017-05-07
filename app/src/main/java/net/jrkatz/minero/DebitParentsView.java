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
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import net.jrkatz.minero.data.Debit;

import java.util.List;

/**
 * @Author jrkatz
 * @Date 5/6/2017.
 */

public class DebitParentsView extends LinearLayout {
    public DebitParentsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void bind(@NonNull final List<Debit> debits) {
        this.removeAllViews();
        if (debits.isEmpty()) {
            throw new IllegalArgumentException("debits list cannot be empty");
        }

        for (final Debit debit : debits) {
            final DebitView view = new DebitView(getContext(), null);
            view.bind(debit);
            addView(view, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        }
    }

    public void unbind() {
        this.removeAllViews();
    }
}
