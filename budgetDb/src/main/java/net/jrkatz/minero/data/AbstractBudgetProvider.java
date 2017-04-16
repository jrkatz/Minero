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

/**
 * @Author jrkatz
 * @Date 4/16/2017.
 */

public abstract class AbstractBudgetProvider<ProviderContext extends IDataContext> {
    @NonNull
    public abstract Budget createBudget(@NonNull final ProviderContext context,
                                      @NonNull final PeriodDefinition periodDefinition,
                                      final long distribution,
                                      @NonNull final String name) throws ProviderException;

    public abstract Budget getBudget(@NonNull final ProviderContext context,
                                     final long id) throws ProviderException;

    public abstract Budget getDefaultBudget(@NonNull final ProviderContext context) throws ProviderException;
}