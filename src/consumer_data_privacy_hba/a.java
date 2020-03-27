/**
 * 
 */
package consumer_data_privacy_hba;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * @author zeeshan
 *
 */
public class a {

	/**
	 * 
	 */
	public a() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String gc ="20.198999999999991";
		System.out.println(gc);
		double d = Double.parseDouble(String.format("%.3f", Double.parseDouble(gc)));  // can be required precision
		System.out.println(d);
		double x = Double.parseDouble(String.format("%.3f", Double.parseDouble(gc))) * 1000.0;
		System.out.println(x);

		
	}

}
