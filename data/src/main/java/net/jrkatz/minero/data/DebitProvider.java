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

package net.jrkatz.minero.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import org.joda.time.DateTime;

/**
 * @Author jrkatz
 * @Date 4/16/2017.
 */

public abstract class DebitProvider<ProviderContext extends IDataContext> {
    @NonNull
    public Debit createDebit(@NonNull final ProviderContext context,
                                      final long budgetId,
                                      final long budgetPeriodId,
                                      final int amount,
                                      @NonNull final String description,
                                      @NonNull final DateTime time) throws ProviderException {

        return createDebit(context, budgetId, budgetPeriodId, amount, description, time, null);
    };


    @Nullable public abstract Debit getDebit(@NonNull final ProviderContext context, final long debitId) throws ProviderException;

    @NonNull public Debit getDebitOrThrow(@NonNull final ProviderContext context, final long debitId) throws ProviderException {
        final Debit debit = getDebit(context, debitId);
        if (debit == null) {
            throw new ProviderException(String.format("No debit found with id %d", debitId));
        }
        return debit;
    }

    @NonNull
    abstract Debit createDebit(@NonNull final ProviderContext context,
                                      final long budgetId,
                                      final long budgetPeriodId,
                                      final int amount,
                                      @NonNull final String description,
                                      @NonNull final DateTime time,
                                      @Nullable final Long parentId) throws ProviderException;

    @NonNull
    public abstract ImmutableList<Debit> readDebits(@NonNull final ProviderContext context,
                                                    final long budgetPeriodId) throws ProviderException;

    public abstract void clearDebits(@NonNull final ProviderContext context) throws ProviderException;

    @NonNull
    public Debit amendDebit(@NonNull final ProviderContext context, long debitId, String description) throws ProviderException {
        final Debit original = getDebitOrThrow(context, debitId);

        return createDebit(
                context,
                original.getBudgetId(),
                original.getBudgetPeriodId(),
                original.getAmount(),
                description,
                DateTime.now(),
                original.getId()
        );
    }

    @NonNull
    public Debit amendDebit(@NonNull final ProviderContext context, long debitId, int newAmount) throws ProviderException {
        final Debit firstParent = getDebitOrThrow(context, debitId);
        Debit parent = firstParent;

        //calculate the current amount this chain of debits represents
        int currentAmount = firstParent.getAmount();
        while (parent.getParentId() != null) {
            parent = getDebitOrThrow(context, parent.getParentId());
            currentAmount += parent.getAmount();
        }

        //calculate the amount needed to reach the target amount.
        final int amount = newAmount - currentAmount;

        //if we're altering a period that has already passed, it has been applied to the running total
        //so that must be updated as well.
        final BudgetPeriod bp = context.getBudgetPeriodProvider().getCurrentBudgetPeriod(context, firstParent.getBudgetId());
        if (bp.getId() != firstParent.getBudgetPeriodId()) {
            context.getBudgetProvider().updateBudgetRunningTotal(
                    context,
                    firstParent.getBudgetId(),
                    amount);
        }

        return createDebit(
                context,
                firstParent.getBudgetId(),
                firstParent.getBudgetPeriodId(),
                amount,
                firstParent.getDescription(),
                DateTime.now(),
                firstParent.getId()
        );
    }
}