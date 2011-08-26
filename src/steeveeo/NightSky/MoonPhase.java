package steeveeo.NightSky;

public class MoonPhase
{
	public static PhaseType getPhase(int check)
	{
		switch(check)
		{
			case 0:
				return PhaseType.FULL_MOON;
			case 1:
				return PhaseType.WANING_GIBBOUS;
			case 2:
				return PhaseType.LAST_QUARTER;
			case 3:
				return PhaseType.WANING_CRESCENT;
			case 4:
				return PhaseType.NEW_MOON;
			case 5:
				return PhaseType.WAXING_CRESCENT;
			case 6:
				return PhaseType.FIRST_QUARTER;
			case 7:
				return PhaseType.WAXING_GIBBOUS;
			default:
				return null;
		}
	}
	
	public static int getPhase(PhaseType phase)
	{
		switch(phase)
		{
			case FULL_MOON:
				return 0;
			case WANING_GIBBOUS:
				return 1;
			case LAST_QUARTER:
				return 2;
			case WANING_CRESCENT:
				return 3;
			case NEW_MOON:
				return 4;
			case WAXING_CRESCENT:
				return 5;
			case FIRST_QUARTER:
				return 6;
			case WAXING_GIBBOUS:
				return 7;
			default:
				return -1;
		}
	}
	
	public static enum PhaseType
	{
		FULL_MOON,
		WANING_GIBBOUS,
		LAST_QUARTER,
		WANING_CRESCENT,
		NEW_MOON,
		WAXING_CRESCENT,
		FIRST_QUARTER,
		WAXING_GIBBOUS
	}
}
