package org.example.groupmanageservice.domain;
import com.gs.fw.finder.Operation;
import java.util.*;
public class ParticipantList extends ParticipantListAbstract
{
	public ParticipantList()
	{
		super();
	}

	public ParticipantList(int initialSize)
	{
		super(initialSize);
	}

	public ParticipantList(Collection c)
	{
		super(c);
	}

	public ParticipantList(Operation operation)
	{
		super(operation);
	}
}
