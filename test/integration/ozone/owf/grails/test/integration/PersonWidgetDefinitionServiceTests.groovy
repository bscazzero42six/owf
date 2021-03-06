package ozone.owf.grails.test.integration

import ozone.owf.grails.services.AutoLoginAccountService
import ozone.owf.grails.domain.Person
import ozone.owf.grails.domain.ERoleAuthority
import ozone.owf.grails.OwfException
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

class PersonWidgetDefinitionServiceTests extends GroovyTestCase {
	
	def widgetDefinitionService
	def personWidgetDefinitionService
	
	protected void setUp() {
        super.setUp()
		def acctService = new AutoLoginAccountService()
		Person p = new Person(username:'testUserWidgetDefinitionServiceTesting', userRealName: 'foo', enabled:true)
		p.save()
		acctService.autoAccountName = 'testUserWidgetDefinitionServiceTesting'
		acctService.autoRoles = [ERoleAuthority.ROLE_ADMIN.strVal]
		widgetDefinitionService.accountService = acctService
		personWidgetDefinitionService.accountService = acctService
    }

    protected void tearDown() {
        super.tearDown()
    }

	def generatePWDPostParamsA()
	{
		def retVal = [
			"guid":"12345678-1234-1234-1234-1234567890a0"
		]
	}

	def generatePWDPostParamsC()
	{
		def retVal = [
			"guid":"12345678-1234-1234-1234-1234567890a1"
		]
	}
	
	def widgetNameParams()
	{
		def retVal = [
		    "widgetName":"My Widget%"
		]
        new GrailsParameterMap(retVal,null);
	}
	
	def pwdParams(pwd)
	{
		def retVal = [
		  "personWidgetDefinition":pwd
		]
	}
	
	def guidAndPersonIdParams(guid, personId)
	{
		def retVal = [
		    "guid":guid,
		    "personId":personId
		]
	}
	
	def nullPwdParams()
	{
		def retVal = [
		"personWidgetDefinition":null
		]
	}

	void testCreate()
	{
		widgetDefinitionService.create(WidgetDefinitionPostParams.generatePostParamsA())
		def resultOfCreate = personWidgetDefinitionService.create(generatePWDPostParamsA())
		assertTrue resultOfCreate.success
		assertEquals "12345678-1234-1234-1234-1234567890a0", resultOfCreate.personWidgetDefinition.widgetDefinition.widgetGuid
		//This tests show too.
		def resultOfShow = personWidgetDefinitionService.show(generatePWDPostParamsA())
		assertEquals resultOfShow.personWidgetDefinition.widgetDefinition.widgetGuid, resultOfCreate.personWidgetDefinition.widgetDefinition.widgetGuid
	}

	void testUpdateForPersonWidgetDefinitionParameter()
	{
		widgetDefinitionService.create(WidgetDefinitionPostParams.generatePostParamsA())
		def response = personWidgetDefinitionService.create(generatePWDPostParamsA())
		
		assertEquals "My Widget", response.personWidgetDefinition.widgetDefinition.displayName
		
		def widgetDefinition = response.personWidgetDefinition.widgetDefinition
		widgetDefinition.displayName = "New Widget Name"
		
		def personWidgetDefinition = response.personWidgetDefinition
		personWidgetDefinition.widgetDefinition = widgetDefinition
		
		response = personWidgetDefinitionService.update(pwdParams(personWidgetDefinition))
		
		assertNotSame "My Widget", response.personWidgetDefinition.widgetDefinition.displayName
		assertEquals "New Widget Name", response.personWidgetDefinition.widgetDefinition.displayName
	}
	
	void testUpdateForGuidAndPersonIdParameters()
	{
		widgetDefinitionService.create(WidgetDefinitionPostParams.generatePostParamsA())
		def response = personWidgetDefinitionService.create(generatePWDPostParamsA())
		
		assertEquals "My Widget", response.personWidgetDefinition.widgetDefinition.displayName
		
		Person person = Person.findByUsername('testUserWidgetDefinitionServiceTesting')
		
		def widgetDefinition = response.personWidgetDefinition.widgetDefinition
		widgetDefinition.displayName = "New Widget Name"
		
		def personWidgetDefinition = response.personWidgetDefinition
		personWidgetDefinition.widgetDefinition = widgetDefinition
		
		response = personWidgetDefinitionService.update(guidAndPersonIdParams(widgetDefinition.widgetGuid, person.id))
		
		assertNotSame "My Widget", response.personWidgetDefinition.widgetDefinition.displayName
		assertEquals "New Widget Name", response.personWidgetDefinition.widgetDefinition.displayName
	}
	
	void testUpdateForNonExistentWidgetDefinitionGuid()
	{
		Person person = Person.findByUsername('testUserWidgetDefinitionServiceTesting')

		shouldFail(OwfException, {
			personWidgetDefinitionService.update(guidAndPersonIdParams('12345678-1234-1234-abcd-1234567890a9', person.id))
		})
	}

	void testList()
	{
		widgetDefinitionService.create(WidgetDefinitionPostParams.generatePostParamsA())
		widgetDefinitionService.create(WidgetDefinitionPostParams.generatePostParamsC())
		personWidgetDefinitionService.create(generatePWDPostParamsA())
		personWidgetDefinitionService.create(generatePWDPostParamsC())
		
		assertEquals 2, personWidgetDefinitionService.list(widgetNameParams()).personWidgetDefinitionList.size()
	}
	
}