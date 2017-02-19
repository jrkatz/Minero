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

package net.jrkatz.minero.budget;

import org.joda.time.LocalDateTime;

/**
 * @Author jrkatz
 * @Date 2/19/2017.
 */
public class SpendEvent {
    private final int mId;
    private final long mAmount;
    private final String mDescription;
    private final LocalDateTime mTime;

    public SpendEvent(int id, long amount, String description, LocalDateTime time) {
        this.mId = id;
        this.mAmount = amount;
        this.mDescription = description;
        this.mTime = time;
    }

    public long getAmount() {
        return mAmount;
    }
}
