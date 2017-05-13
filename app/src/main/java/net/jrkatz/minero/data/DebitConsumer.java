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

import org.joda.time.DateTime;

/**
 * @Author jrkatz
 * @Date 5/13/2017.
 */

public abstract class DebitConsumer {
    @Nullable final IDataChangeListener mChangeListener;

    public DebitConsumer(@Nullable IDataChangeListener dataChangeListener) {
        mChangeListener = dataChangeListener;
    }
    //TODO better name here...
    protected abstract boolean _consume(final int amount, final String description);

    public final boolean consume(final int amount, final String description) {
        final boolean consumed = _consume(amount, description);
        if (consumed && mChangeListener != null) {
            mChangeListener.dataChanged();
        }

        return consumed;
    }

    public abstract static class DebitCreator extends DebitConsumer {
        private final long mBudgetId;
        public DebitCreator(final @NonNull IDataChangeListener dataChangeListener, final long budgetId) {
            super(dataChangeListener);
            mBudgetId = budgetId;
        }

        protected abstract IDataContext getDataContext();

        @Override
        protected final boolean _consume(int amount, String description) {
            try (IDataContext providerContext = getDataContext()) {
                final BudgetPeriod budgetPeriod = providerContext.getBudgetPeriodProvider().getCurrentBudgetPeriod(providerContext, mBudgetId);
                providerContext.getDebitProvider().createDebit(
                        providerContext,
                        budgetPeriod.getBudgetId(),
                        budgetPeriod.getId(),
                        amount,
                        description,
                        DateTime.now());
                providerContext.markSuccessful();
            } catch (ProviderException e) {
                //TODO handle better
                throw new RuntimeException(e);
            }
            return true;
        }
    }
}
