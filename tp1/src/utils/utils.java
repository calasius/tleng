package utils;

import java.util.HashSet;
import java.util.Set;

public class utils {
	
	
	public static <T> Set<T> intersection(Set<T> set1, Set<T> set2){
		Set<T> interceccion = new HashSet<T>();
		if (set2 == null) {
			return interceccion;
		}
		for (T t : set2) {
			if (set1.contains(t)) {
				interceccion.add(t);
			}
		}
		
		return interceccion;
	}
	
	public static <T> Set<T> union(Set<T> set1, Set<T> set2) {
		Set<T> union = new HashSet<T>();
		union.addAll(set1);
		union.addAll(set2);
		return union;
	}

}
