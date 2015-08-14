package de.pspaeth.dla;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.pspaeth.dla.data.Raum;
import de.pspaeth.dla.data.Staub;

public class RaumTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRaum() {
		Raum raum = new Raum(1000, 1000);
		assertEquals(1, raum.anzahlFest());
		assertEquals(0, raum.anzahlBeweglich());
	}

	@Test
	public void testRandomStaub() {
		Raum raum = new Raum(1000, 1000);
		Staub stb = raum.randomStaub();
		assertEquals(1, raum.anzahlBeweglich());
		assertEquals(1, raum.anzahlFest());
	}

	@Test
	public void testErmittleProspektive() {
		Raum raum = new Raum(1000, 1000);
		Collection<Staub> bewgl = raum.getBeweglich();
		
		Staub stb1 = new Staub("s1", 200 * 0.001, 250 * 0.001);
		bewgl.add(stb1);
		assertEquals(1, raum.anzahlBeweglich());
		assertEquals(1, raum.anzahlFest());
		
		raum.macheFest(stb1, 0);
		assertEquals(0, raum.anzahlBeweglich());
		assertEquals(2, raum.anzahlFest());
		
		Staub stb2 = new Staub("s2", 200 * 0.001, 250 * 0.001);
		assertEquals(Raum.Prospektive.BESETZT, raum.ermittleProspektive(stb2));
		
		// Nur falls Diagonalen mitzählen
		//Stream.of(new int[]{199,250},new int[]{199,249}, new int[]{200,249}, new int[]{201,249}, 
		//		new int[]{201,250}, new int[]{201,251}, new int[]{200,251}, new int[]{199,251}).forEach(arr -> {
		//	stb2.setZeit(arr[0]*0.001);
		//	stb2.setTon(arr[1]*0.001);
		//	assertEquals(Raum.Prospektive.KANN_KLEBEN, raum.ermittleProspektive(stb2));			
		//});
		Stream.of(new int[]{199,250}, new int[]{200,249}, new int[]{201,250}, new int[]{200,251}).forEach(arr -> {
			stb2.setZeit(arr[0]*0.001);
			stb2.setTon(arr[1]*0.001);
			assertEquals(Raum.Prospektive.KANN_KLEBEN, raum.ermittleProspektive(stb2));			
		});
		
		IntStream.range(0,1000).forEach( i -> {
			stb2.setZeit(i * 0.001);
			stb2.setTon(248 * 0.001);
			assertEquals(Raum.Prospektive.FREI, raum.ermittleProspektive(stb2));			
			stb2.setTon(252 * 0.001);
			assertEquals(Raum.Prospektive.FREI, raum.ermittleProspektive(stb2));			

			stb2.setZeit(198 * 0.001);
			stb2.setTon(i * 0.001);
			assertEquals(Raum.Prospektive.FREI, raum.ermittleProspektive(stb2));			
			stb2.setZeit(202 * 0.001);
			assertEquals(Raum.Prospektive.FREI, raum.ermittleProspektive(stb2));			
		});
	}

	@Test
	public void testQuantisiere() {
		// fail("Not yet implemented");
	}

}
