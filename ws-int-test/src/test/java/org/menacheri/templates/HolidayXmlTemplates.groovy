package org.menacheri.templates

/**
 * Class shows how easy it is to get xml templates in Groovy. The triple """ is a god send 
 * and helps creating xml request response templates easy. Using the ${} notation it become 
 * easy to change the value of the generated xml dynamically and send new ones. 
 * @author Abraham Menacherry
 *
 */
class HolidayXmlTemplates 
{
	static final String NAMESPACE = "http://menacheri.org/hr/schemas";
	String startDate = "2011-11-10";
	String endDate = "2011-11-15";
	String empId = "42";
	String firstName = "Abraham";
	String lastName = "Menacherry";
	String status = "SUCCESS";
	
	public String getHolidayRequest()
	{
		String holidayRequest =
		"""
		<HolidayRequest xmlns="${NAMESPACE}">
			<HolidayDates>
				<StartDate>${startDate}</StartDate>
				<EndDate>${endDate}</EndDate>
			</HolidayDates>
			<Employee>
				<Number>${empId}</Number>
				<FirstName>${firstName}</FirstName>
				<LastName>${lastName}</LastName>
			</Employee>
		</HolidayRequest>
		"""
		return holidayRequest;
	}
	
	public String getHolidayResponse()
	{
		String holidayResponse =
		"""
		<HolidayResponse xmlns="${NAMESPACE}">
			<Employee>
				<Number>${empId}</Number>
				<FirstName>${firstName}</FirstName>
				<LastName>${lastName}</LastName>
			</Employee>
			<Status>${status}</Status>
		</HolidayResponse>
		"""
		return holidayResponse;
	}
}
