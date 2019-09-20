require_relative "solution"
require "test/unit"

class TestSolution < Test::Unit::TestCase

  def test_simple
    n = 1
    str_in = 'abc'
    str = str_in.split('')
    res = n_times_sol(str, n)
    assert_equal(%w(a b c), res)
  end

  def test_simple_rec
    n = 1
    str_in = 'abc'
    str = str_in.split('')
    res = recursive_solution('', str, n)
    assert_equal(%w(a b c), res)
  end

  def test_abc_2
    n = 2
    str_in = 'abc'
    str = str_in.split('')
    res = n_times_sol(str, n)
    assert_equal(%w(ab ac ba bc ca cb), res)
  end

  def test_abc_2_rec
    n = 2
    str_in = 'abc'
    str = str_in.split('')
    res = recursive_solution('', str, n)
    assert_equal(%w(ab ac ba bc ca cb), res)
  end


  def test_abc_empty
    n = 0
    str_in = 'erertertert'
    str = str_in.split('')
    res = n_times_sol(str, n)
    assert_equal([], res)
  end

  def test_abc_empty_rec
    n = 0
    str_in = 'erertertert'
    str = str_in.split('')
    res = recursive_solution('', str, n)
    assert_equal([], res)
  end

  def test_abc_empty_in
    n = 30
    str_in = ''
    str = str_in.split('')
    res = n_times_sol(str, n)
    assert_equal([], res)
  end

  def test_abc_empty_in_rec
    n = 30
    str_in = ''
    str = str_in.split('')
    res = recursive_solution('', str, n)
    assert_equal([], res)
  end

  def test_abc_aa
    n = 1
    str_in = 'aa'
    str = str_in.split('')
    res = n_times_sol(str, n)
    assert_equal(['a'], res)
  end

  def test_abc_aa_rec
    n = 1
    str_in = 'aa'
    str = str_in.split('')
    res = recursive_solution('', str, n)
    assert_equal(['a'], res)
  end





end