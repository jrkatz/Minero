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
import android.view.View;
import android.widget.FrameLayout;

/**
 * @Author jrkatz
 * @Date 5/6/2017.
 */

public class DebitListEntryView extends FrameLayout {

    public DebitListEntryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.view_debit_list_entry, this);
    }

    public void bind(@NonNull final DebitListEntry entry, final boolean expanded) {
        final DebitView debitView = (DebitView) findViewById(R.id.debit_view);
        if (entry.getAmount() == 0) {
            setBackgroundColor(getResources().getColor(R.color.budget_negative, getContext().getTheme()));
        } else {
            setBackgroundColor(getResources().getColor(R.color.light_background, getContext().getTheme()));
        }
        debitView.bind(entry);
        setCollapsed(entry);
        if (expanded) {
            setExpanded(entry);
        } else {
            setCollapsed(entry);
        }
    }

    private void showParents(final DebitListEntry entry, final DebitParentsView parentsView) {
        if (entry.getDebits().size() > 1) {
            parentsView.bind(entry.getDebits());
            parentsView.setVisibility(View.VISIBLE);
        }
        else {
            hideParents(parentsView);
        }
    }

    private void hideParents(final DebitParentsView parentsView) {
        parentsView.unbind();
        parentsView.setVisibility(View.GONE);
    }

    protected void setCollapsed(final DebitListEntry entry) {
        final DebitView debitView = (DebitView) findViewById(R.id.debit_view);
        final DebitParentsView parentsView = (DebitParentsView) findViewById(R.id.parents_view);
        hideParents(parentsView);
        if (entry.getAmount() == 0) {
            debitView.setVisibility(View.GONE);
        }
        else {
            debitView.setVisibility(View.VISIBLE);
        }
    }

    protected void setExpanded(final DebitListEntry entry) {
        final DebitView debitView = (DebitView) findViewById(R.id.debit_view);
        final DebitParentsView parentsView = (DebitParentsView) findViewById(R.id.parents_view);
        showParents(entry, parentsView);
        debitView.setVisibility(View.VISIBLE);
    }
}
