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

import com.google.common.collect.ImmutableList;

import org.joda.time.DateTime;

/**
 * @Author jrkatz
 * @Date 4/16/2017.
 */

public abstract class AbstractDebitProvider<ProviderContext extends IDataContext> {
    @NonNull
    public abstract Debit createDebit(@NonNull final ProviderContext context,
                                      final long budgetId,
                                      final long budgetPeriodId,
                                      final int amount,
                                      @NonNull final String description,
                                      @NonNull final DateTime time) throws ProviderException;

    @NonNull
    public abstract ImmutableList<Debit> readDebits(@NonNull final ProviderContext context,
                                                    final long budgetPeriodId) throws ProviderException;

    public abstract void clearDebits(@NonNull final ProviderContext context) throws ProviderException;
}
