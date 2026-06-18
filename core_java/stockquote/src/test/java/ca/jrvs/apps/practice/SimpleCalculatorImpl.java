package ca.jrvs.apps.practice;

public class SimpleCalculatorImpl implements SimpleCalculator {

  @Override
  public int add(int x, int y) {
    // TODO Auto-generated method stub
    return x + y;
  }

  @Override
  public int subtract(int x, int y) {
    // TODO Auto-generated method stub
    return x - y;
  }

  @Override
  public int multiply(int x, int y) {
    // TODO Auto-generated method stub
    return x * y;
  }

  @Override
  public double divide(int x, int y) {
    // TODO Auto-generated method stub
    return (double) x /y;
  }

  public int power(int x, int y) {
    // TODO Auto-generated method stub
    return 0;
  }

  public double abs(double x) {
    // TODO Auto-generated method stub
    return Math.abs(x);
  }

  public static void main(String[] args){
    SimpleCalculatorImpl test = new SimpleCalculatorImpl();
    System.out.println(test.add(1,2));
  }

}