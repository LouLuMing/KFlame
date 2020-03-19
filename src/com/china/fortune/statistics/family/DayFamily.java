package com.china.fortune.statistics.family;

import com.china.fortune.common.DateAction;

public class DayFamily implements FamilyIdInterface {
	@Override
	public int familyId(long ticket) {
		return DateAction.getDays(ticket);
	}

	@Override
	public int nextFamilyId(int familyId) {
		return familyId+1;
	}
}
