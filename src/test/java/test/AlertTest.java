package test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import es.codeurjc.ais.tictactoe.WebApp;


/*
 * Pruebas de sistema de la aplicación con Selenium. 
 * 
 * Para simular una partida el test iniciará dos navegadores web de forma simultánea
e irá interactuando con ellos de forma alternativa. 

De esta forma, puede simular un partida por turnos. El juego está
implementado de forma que al finalizar el mismo, el resultado aparece en un cuadro de
diálogo (alert). 

El objetivo de los tests consiste en verificar que el mensaje del alert es el
esperado cuando gana cada uno de los jugadores y cuando quedan empate.
Para obtener el mensaje del alert se utiliza el código:
browser1.switchTo().alert().getText()
 */
@DisplayName("Pruebas de sistema: alertTest")
class AlertTest {
	protected WebDriver driver;
	protected WebDriver driver2;

	@BeforeAll
	public static void setupClass() {
		System.setProperty("webdriver.gecko.driver", "/home/eclipse/geckodriver-v0.26.0-linux64/geckodriver");
		WebApp.start();
	}

	@BeforeEach
	public void setupTest() {
		driver = new FirefoxDriver();
		driver2 = new FirefoxDriver();
	}

	@AfterEach
	public void teardown() {
		if (driver != null) {
			driver.quit();
		}
		if (driver2 != null) {
			driver2.quit();
		}
	}
	@AfterAll
	public static void teardownClass() {
		WebApp.stop();
	}
	
	/**
	 * Proveedor de argumentos para el metodo test.
	 * @return Stream<Arguments>
	 */
	static Stream<Arguments> turnosProvider() {
	    return Stream.of(
	    		Arguments.of(-1, Arrays.asList(0,1,2,3,5,4,7,8,6)),
	    		Arguments.of(-1, Arrays.asList(0,3,1,2,5,4,6,7,8)),
	    		Arguments.of(-1, Arrays.asList(4,0,3,5,2,6,8,1,7)),
	    		Arguments.of(0, Arrays.asList(0,8,1,7,2)),
	    		Arguments.of(0, Arrays.asList(4,1,0,5,8)),
	    		Arguments.of(0, Arrays.asList(4,0,1,5,7)),
	    		Arguments.of(1, Arrays.asList(0,3,8,4,2,5)),
	    		Arguments.of(1, Arrays.asList(0,4,3,6,1,2)),
	    		Arguments.of(1, Arrays.asList(4,0,1,3,5,6))
	    );
	}
	
	/**
	 * Casos de prueba para el metodo getCellsIfWinner.
	 * Comprueba que el metodo devuelve null si ningun jugador gana al finalizar los turnos.
	 * 
	 */
	@ParameterizedTest
	@MethodSource("turnosProvider")
	@DisplayName("AlertText Test")
	void test(int jugGanador, List<Integer> turnos) {
		//GIVEN
		driver.get("http://localhost:8080/");
		driver2.get("http://localhost:8080/");

		WebDriverWait wait = new WebDriverWait(driver, 5); // Configuracion seconds
		WebDriverWait wait2 = new WebDriverWait(driver2, 5); // seconds
		
		// Locate single element
		WebElement startBtn = driver.findElement(By.id("startBtn"));
		WebElement nickname = driver.findElement(By.id("nickname"));
		
		nickname.sendKeys("Jugador1");
		wait.until(ExpectedConditions.elementToBeClickable(By.id("startBtn")));
		startBtn.click();
		
		WebElement startBtn2 = driver2.findElement(By.id("startBtn"));
		WebElement nickname2 = driver2.findElement(By.id("nickname"));
		
		nickname2.sendKeys("Jugador2");
		wait2.until(ExpectedConditions.elementToBeClickable(By.id("startBtn")));
		startBtn2.click();
		
		//Turnos de los jugadores. Comprueba que se marca y el turno cambia
		int i = 0;

		WebElement celda = null;
		for(Integer turno: turnos) {
			if(i%2 == 0) {	//Marca jugador 1
				wait.until(ExpectedConditions.elementToBeClickable(By.id("cell-"+turno)));
				celda = driver.findElement(By.id("cell-"+turno));
			}else {			//Marca jugador 2
				wait2.until(ExpectedConditions.elementToBeClickable(By.id("cell-"+turno)));
				celda = driver2.findElement(By.id("cell-"+turno));
			}
			celda.click();
			i++;
		}

		String textoAlert1 = driver.switchTo().alert().getText();
		String textoAlert2 = driver2.switchTo().alert().getText();
		if(jugGanador == -1) { //empate
			assertThat(textoAlert1, is("Draw!"));
			assertThat(textoAlert2, is("Draw!"));
		}else if(jugGanador == 0) {  //gana jugador 1
			assertThat(textoAlert1, is("Jugador1 wins! Jugador2 looses."));
			assertThat(textoAlert2, is("Jugador1 wins! Jugador2 looses."));
		}else {						 //gana jugador 2
			assertThat(textoAlert1, is("Jugador2 wins! Jugador1 looses."));
			assertThat(textoAlert2, is("Jugador2 wins! Jugador1 looses."));
		}

		
	}

}
