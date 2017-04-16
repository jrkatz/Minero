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

package net.jrkatz.minero.data.period;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import org.joda.time.LocalDate;

/**
 * @Author jrkatz
 * @Date 2/19/2017.
 */
public abstract class PeriodDefinition implements Parcelable {
    enum Type {
        MONTHLY {
            @Override
            PeriodDefinition parse(@NonNull String str) {
                return MonthlyPeriodDefinition.parseContent(str);
            }
        }
        ;

        abstract PeriodDefinition parse(@NonNull final String str);
    }

    public abstract Period periodForDate(final LocalDate date);

    public final Period nextPeriod(final Period period) {
        return periodForDate(period.getEnd());
    }

    public final Period previousPeriod(final Period period) {
        return periodForDate(period.getStart().minusDays(1));
    }

    abstract Type getType();

    @NonNull
    public abstract String serializeContent();

    @NonNull
    public final String serialize() {
        return String.format("%s:%s", getType(), serializeContent());
    }

    @NonNull
    public static PeriodDefinition deserialize(@NonNull final String str) throws PeriodParseException {
        try {
            final int delim = str.indexOf(":");
            final String typeStr = str.substring(0, delim);

            final Type t = Type.valueOf(typeStr);
            return t.parse(str.substring(delim + 1));
        }
        catch (Exception e) {
            throw new PeriodParseException(e);
        }
    }

    public static class PeriodParseException extends Exception {
        PeriodParseException(Throwable cause) {
            super("Couldn't parse PeriodDefinition", cause);
        }
    }
}
