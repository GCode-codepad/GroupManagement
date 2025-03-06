package org.example.groupmanageservice.domain;
import com.gs.fw.finder.Operation;
import java.util.*;
public class RoomList extends RoomListAbstract
{
	public RoomList()
	{
		super();
	}

	public RoomList(int initialSize)
	{
		super(initialSize);
	}

	public RoomList(Collection c)
	{
		super(c);
	}

	public RoomList(Operation operation)
	{
		super(operation);
	}
}
