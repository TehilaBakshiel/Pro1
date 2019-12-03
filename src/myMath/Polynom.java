package myMath;

import java.awt.Point;
import myMath.Monom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Predicate;

import myMath.Monom;
/**
 * This class represents a Polynom with add, multiply functionality, it also should support the following:
 * 1. Riemann's Integral: https://en.wikipedia.org/wiki/Riemann_integral
 * 2. Finding a numerical value between two values (currently support root only f(x)=0).
 * 3. Derivative
 * 
 * @author Boaz
 *
 */
public class Polynom implements Polynom_able{
	ArrayList<Monom> polynom;	
	private static final Monom_Comperator compare = new Monom_Comperator();

	/**
	 * Zero (empty polynom)
	 */
	public Polynom() {
		polynom = new ArrayList<Monom>();
	}
	public Polynom(Polynom pol) {    // Copy constructor
		polynom = new ArrayList<>();
		Iterator<Monom> it = pol.iteretor();
		while(it.hasNext()) {
			Monom s = new Monom(it.next());
			polynom.add(s);
		}
	}

	/**
	 * init a Polynom from a String such as:
	 *  {"x", "3+1.4X^3-34x","8x^4+3x^3-4x^2+1"};
	 *  Invalid Polynom such as:
	 *  {"(2x^2-4)*(-1.2x-7.1)", "(3-3.4x+1)*((3.1x-1.2)-(3X^2-3.1))"};
	 * @param s: is a string represents a Polynom
	 */
	public Polynom(String s) {
		if(s.contains("(") || s.contains(")")) {
			throw new RuntimeException("Error. Polynom cannot contains the char ( or ) ");
		}
		else {
			polynom = new ArrayList<Monom>();
			if(s.startsWith("+")) {
				s = s.replaceFirst("\\+", "");
			}
			String[] p1 = s.split("[+]");
			boolean flag = false;
			for(int i = 0; i < p1.length; i++){
				String[] p2 = p1[i].split("-");
				for(int j = 0; j < p2.length; j++){
					if(p2[j].equals("")) {
						flag = true;
					}
					else {
						if(p2.length == 1){ //if the array is sized 1 than the Monom is surely positive.
							polynom.add(new Monom(p2[j]));
							break;
						}
						if(flag){ //case 1: first slot is empty -> 2nd slot is a negative monom
							polynom.add(new Monom("-" + p2[j]));

							flag = false;
						}	
						else if(j == 0 && !flag){ //case 2: first slot is not empty -> 1st slot is a positive monom
							polynom.add(new Monom(p2[j]));

						}
						else { //after spliting by "-" the rest are negative.
							polynom.add(new Monom("-" + p2[j]));	
						}
					}	
				}

			} //sort so the polynom is printed correctly.
			polynom= sortAndMerge(polynom);
		}
	}

	/**
	 * sort the polynom
	 * @param list: is a Arraylist of Monoms represents a Polynom
	 */
	private ArrayList<Monom> sortAndMerge(ArrayList<Monom> list){	
		ArrayList<Monom> s = new ArrayList<Monom>();
		for (int i = 0; i < list.size(); i++) {
			Monom temp = list.get(i);
			for (Iterator<Monom> iterator = s.iterator(); iterator.hasNext();) {
				Monom monom = (Monom) iterator.next();
				if(monom.get_power() == temp.get_power()){
					temp.add(monom);
					iterator.remove();
				}
			}
			if(temp.get_coefficient() != 0.0) {
				s.add(temp);
			}
		}
		s.sort(compare);
		return s;
	}

	@Override
	public double f(double x) {
		double ans = 0;
		Iterator<Monom> it= this.iteretor();
		while(it.hasNext()) {
			Monom m = it.next();
			ans += m.f(x);
		}
		return ans;
	}

	@Override
	public void add(Polynom_able p1) {
		Iterator<Monom> itP1 = p1.iteretor();
		while(itP1.hasNext()){
			Monom takeMon = itP1.next();
			this.add(takeMon);
		}
		polynom = sortAndMerge(polynom);
	}

	@Override
	public void add(Monom m1) {
		// TODO Auto-generated method stub
		boolean found=false;
		Monom temp = null;
		Iterator<Monom> mymonom = this.iteretor();
		while(mymonom.hasNext()){
			temp = mymonom.next();
			if(temp.get_power()==m1.get_power()){
				temp.add(m1);
				found = true;
				break;
			}
		}
		if(!found){
			polynom.add(m1);
			polynom = sortAndMerge(polynom);
		}
	}
	/**
	 * Subtract p1 from this Polynom
	 * @param p1 - input polynom
	 */
	@Override
	public void substract(Polynom_able p1) {
		Iterator<Monom> itrp1 =p1.iteretor();
		while (itrp1.hasNext()) {
			Monom m2=itrp1.next();
			double a = ((m2.get_coefficient())* (-1));
			int b = m2.get_power();
			Monom n1 = new Monom (a,b);
			add(n1);
		}
	}

	@Override
	public void multiply(Polynom_able p1) {
		Polynom polynomnew= new Polynom();
		Iterator<Monom> itrp1 = p1.iteretor();
		while (itrp1.hasNext()) {
			Monom n1 = new Monom(itrp1.next());
			Iterator<Monom> itr = this.iteretor();
			while (itr.hasNext()) {
				Monom n3= new Monom (n1);
				Monom n2 = new Monom (itr.next());
				n3.multipy(n2);
				if (n3.get_coefficient()==0) {
					itr.remove();
				}
				polynomnew.add(n3);
			}
		}
		this.polynom=polynomnew.polynom; 
		Iterator<Monom> iter = this.iteretor();
		while (iter.hasNext()) {
			Monom n1 = new Monom(iter.next());
			if (n1.get_coefficient()==0) {
				iter.remove();
			}
		}
	}

	@Override
	public boolean equals(Polynom_able p1) {
		boolean answer = false;
		Iterator<Monom> thisIter = this.iteretor();
		Iterator<Monom> p1Iter = p1.iteretor();
		while(thisIter.hasNext() && p1Iter.hasNext()){
			answer = true;
			Monom m0 = thisIter.next();
			Monom m1 = p1Iter.next();
			if(!m0.equals(m1)){
				answer = false;
				break;
			}
		}
		return answer;
	}

	/**
	 * check if the polynom equals zero
	 * return true or false
	 */
	@Override
	public boolean isZero() {
		Iterator<Monom> thisIter = this.iteretor();
		if(thisIter.next().get_coefficient() == 0) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public double root(double x0, double x1, double eps) {
		if(f(x0)*f(x1) <= 0) { 
			double x2=(x1+x0)/2;
			if(0-eps<=f(x2) && 0+eps>f(x2)) {
				return x2;
			}
			else {
				if((f(x2)<0 && f(x0)<0)||(f(x2)>0 && f(x0)>0)){
					x0=x2;
					return root(x0,x1,eps);
				}
				else {
					return root(x0,x2,eps);
				}
			}
		}
		else { //if(f(x0)*f(x1)>0) 
			throw new RuntimeException("worng input");
		}
	}

	@Override
	public Polynom_able copy() {
		return new Polynom(this);
	}

	@Override
	public Polynom_able derivative() {
		Polynom_able ablep1= new Polynom();
		Iterator<Monom> itr= iteretor();
		while(itr.hasNext()) {
			Monom m1= new Monom(itr.next());
			Monom m2= new Monom ();
			m2=m1.derivative();
			ablep1.add(m2);	
		}
		Iterator<Monom> iter = ablep1.iteretor();
		while (iter.hasNext()) {
			Monom n1 = new Monom(iter.next());
			if (n1.get_coefficient()==0) {
				iter.remove();
			}
		}
		return ablep1;
	}

	@Override
	public double area(double x0, double x1, double eps) {
		if (x0>x1){
			throw new RuntimeException("worng input");
		}
		double result = 0;
		while (x0<=x1) {
			if (f(x0)<0) {
				x0=x0+eps;
			}
			else {
				result = result + (f(x0)*eps);
				x0=x0+eps;
			}
		}
		return result;
	}

	@Override
	public Iterator<Monom> iteretor() {
		return this.polynom.iterator();		
	}
	@Override
	public void multiply(Monom m1) {
		Polynom p1 = new Polynom();
		if(m1.get_coefficient()==0) {
			this.polynom.clear();
		}
		else {
			//	Monom m2 = new Monom(m1);
			Iterator<Monom> iter = this.iteretor();
			while(iter.hasNext()) {
				Monom m = iter.next();
				m.multipy(m1);
				p1.add(m);
			}
		}
	}

	/**
	 * toString function to print a Polynom
	 * in descecnding order
	 * @return string of this polynom
	 */
	public String toString() {
		String ans = "";
		Iterator<Monom> it = polynom.iterator();
		if(!it.hasNext()) {
			return "0.0";
		}
		ans= ans+it.next();
		while(it.hasNext()) {
			Monom m1 = it.next();
			if(m1.get_coefficient()>0) {
				ans= ans+" + "+m1.toString();
			}
			else {
				ans= ans+" "+ m1.toString();
			}
		}
		if (ans!="") {
			return ans;
		}
		else {
			return 0+"";
		}
	}
}
