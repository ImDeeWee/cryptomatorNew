package org.cryptomator.ui.error;

import org.cryptomator.common.Environment;
import org.cryptomator.common.ErrorCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.Optional;
import javafx.application.HostServices;


class ErrorControllerTest {

	Application application;
	String stackTrace;
	ErrorCode errorCode;
	Scene previousScene;
	Stage window;
	Environment environment;
	ExecutorService executorService;
	ErrorController errorController;


	@BeforeEach
	public void beforeEach() {
		application = Mockito.mock(Application.class);
		stackTrace = "This is a stackTrace mock";
		errorCode = Mockito.mock(ErrorCode.class);
		previousScene = Mockito.mock(Scene.class);
		window = Mockito.mock(Stage.class);
		environment = Mockito.mock(Environment.class);
		executorService = Mockito.mock(ExecutorService.class);
		errorController = new ErrorController(application, stackTrace, errorCode, previousScene, window, environment, executorService);
	}

	private ErrorDiscussion createErrorDiscussion(String title, int upvoteCount, ErrorDiscussion.Answer answer) {
		ErrorDiscussion ed = new ErrorDiscussion();
		ed.title = title;
		ed.upvoteCount = upvoteCount;
		ed.answer = answer;
		return ed;
	}

	@DisplayName("compare error discussions by upvote count")
	@ParameterizedTest
	@CsvSource(textBlock = """
			10, <, 5 
			8, >, 15 
			10, =, 10
			""")
	public void testCompareUpvoteCount(int leftUpvoteCount, char expected, int rightUpvoteCount) {
		int expectedResult = switch (expected) {
			case '<' -> -1;
			case '>' -> +1;
			default -> 0;
		};
		var left = createErrorDiscussion("", leftUpvoteCount, null);
		var right = createErrorDiscussion("", rightUpvoteCount, null);
		int result = errorController.compareUpvoteCount(left, right);
		Assertions.assertEquals(expectedResult, Integer.signum(result));
	}

	@DisplayName("compare error discussions by existence of an answer")
	@ParameterizedTest
	@CsvSource(textBlock = """
			false, =, false
			true, =, true
			true, <, false
			false, >, true
			""")
	public void testCompareIsAnswered(boolean leftIsAnswered, char expected, boolean rightIsAnswered) {
		var answer = new ErrorDiscussion.Answer();
		int expectedResult = switch (expected) {
			case '<' -> -1;
			case '>' -> +1;
			default -> 0;
		};
		var left = createErrorDiscussion("", 0, leftIsAnswered ? answer : null);
		var right = createErrorDiscussion("", 0, rightIsAnswered ? answer : null);
		int result = errorController.compareIsAnswered(left, right);
		Assertions.assertEquals(expectedResult, result);
	}

	@DisplayName("compare error codes by full error code")
	@ParameterizedTest
	@CsvSource(textBlock = """
			Error 0000:0000:0000, =, Error 0000:0000:0000
			Error 6HU1:12H1:HU7J, <, Error 0000:0000:0000
			Error 0000:0000:0000, >, Error 6HU1:12H1:HU7J
			""")
	public void testCompareByFullErrorCode(String leftTitle, char expected, String rightTitle) {
		Mockito.when(errorCode.toString()).thenReturn("6HU1:12H1:HU7J");
		int expectedResult = switch (expected) {
			case '<' -> -1;
			case '>' -> +1;
			default -> 0;
		};
		var left = createErrorDiscussion(leftTitle, 0, null);
		var right = createErrorDiscussion(rightTitle, 0, null);
		int result = errorController.compareByFullErrorCode(left, right);
		Assertions.assertEquals(expectedResult, result);
	}

	@DisplayName("compare error codes by root cause")
	@ParameterizedTest
	@CsvSource(textBlock = """
			Error 6HU1:12H1:0000, =, Error 6HU1:12H1:0000
			Error 6HU1:12H1:0007, =, Error 6HU1:12H1:0042
			Error 0000:0000:0000, =, Error 0000:0000:0000
			Error 6HU1:12H1:0000, <, Error 0000:0000:0000
			Error 6HU1:12H1:0000, <, Error 6HU1:0000:0000
			Error 0000:0000:0000, >, Error 6HU1:12H1:0000
			Error 6HU1:0000:0000, >, Error 6HU1:12H1:0000
			""")
	public void testCompareByRootCauseCode(String leftTitle, char expected, String rightTitle) {
		Mockito.when(errorCode.methodCode()).thenReturn("6HU1");
		Mockito.when(errorCode.rootCauseCode()).thenReturn("12H1");
		int expectedResult = switch (expected) {
			case '<' -> -1;
			case '>' -> +1;
			default -> 0;
		};
		var left = createErrorDiscussion(leftTitle, 0, null);
		var right = createErrorDiscussion(rightTitle, 0, null);
		int result = errorController.compareByRootCauseCode(left, right);
		Assertions.assertEquals(expectedResult, result);
	}

	@DisplayName("check if the error code contains the method code")
	@ParameterizedTest
	@CsvSource(textBlock = """
			Error 6HU1:0000:0000, true
			Error 0000:0000:0000, false
			""")
	public void testContainsMethodCode(String title, boolean expectedResult) {
		Mockito.when(errorCode.methodCode()).thenReturn("6HU1");
		var ed = createErrorDiscussion(title, 0, null);
		boolean result = errorController.containsMethodCode(ed);
		Assertions.assertEquals(expectedResult, result);
	}


	/**
	* Vérifie que la méthode reportError() génère correctement l'URL du rapport d'erreur formaté.
	* Il simule l'environnement, l'OS, et le numéro de version, puis vérifie que l'URL générée correspond à l'URL attendue,
	* en s'assurant que la méthode application.getHostServices().showDocument() est appelée avec l'URL correcte.
	*/
	@DisplayName("reportError() ouvre l'URL correcte avec le rapport d'erreur formaté")
	@Test
	public void testReportError() {

		HostServices hostServices = Mockito.mock(HostServices.class);
		Mockito.when(application.getHostServices()).thenReturn(hostServices);
		Mockito.when(errorCode.toString()).thenReturn("ERR123");
		Mockito.when(environment.getAppVersion()).thenReturn("1.0.0");
		Mockito.when(environment.getBuildNumber()).thenReturn(Optional.of("1001"));

		errorController.reportError();

		String expectedUrl = "https://github.com/cryptomator/cryptomator/discussions/new?category=Errors&title=Error+ERR123&body=" +
			URLEncoder.encode("<!-- 💚 Thank you for reporting this error. -->\n" +
				"OS: " + System.getProperty("os.name") + " / " + System.getProperty("os.version") + "\n" +
				"App: 1.0.0 / 1001\n" +
				"\n" +
				"<!-- ✏ Please describe what happened as accurately as possible. -->\n" +
				"Description:\n" +
				"\n" +
				"<!-- 📋 Please also copy and paste the details from the error window. -->\n" +
				"Details:\n" +
				"\n" +
				"<!-- ❗ If the description or the detail text is missing, the discussion will be deleted. -->\n", 
				StandardCharsets.UTF_8);

		Mockito.verify(hostServices).showDocument(expectedUrl);
	}

	/**
	* Vérifie que la méthode searchError() génère l'URL de recherche d'erreur correctement en fonction du code d'erreur donné.
	* Il s'assure que le formatage de l'URL avec le code d'erreur est correct,
	* et que la méthode application.getHostServices().showDocument() est appelée avec l'URL générée.
	*/
	@DisplayName("searchError() ouvre l'URL correcte pour la recherche d'erreur")
	@Test
	public void testSearchError() {

		HostServices hostServices = Mockito.mock(HostServices.class);
		Mockito.when(application.getHostServices()).thenReturn(hostServices);
		Mockito.when(errorCode.toString()).thenReturn("ERR123:456");

		errorController.searchError();

		String expectedUrl = "https://github.com/cryptomator/cryptomator/discussions/categories/errors?discussions_q=category:Errors+ERR123+OR+456";
		Mockito.verify(hostServices).showDocument(expectedUrl);
	}




}