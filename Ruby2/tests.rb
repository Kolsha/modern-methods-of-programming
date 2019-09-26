require_relative "solution"
require "test/unit"
require "benchmark"

$slow_executor = Proc.new do |x|
  sleep(0.1)
  x
end

class TestSolution < Test::Unit::TestCase

  TEST_ARRAY_SIZE = 50
  def test_all
    a = [*1..TEST_ARRAY_SIZE]
    sync = nil
    s_time = Benchmark.measure {
      sync = a.all? &$slow_executor
    }
    parallel = nil
    p_time = Benchmark.measure {
      parallel = a.p_all? &$slow_executor
    }

    assert_true(s_time.real > p_time.real)

    assert_equal(sync, parallel)
  end

  def test_any
    a = [*1..TEST_ARRAY_SIZE]
    sync = nil
    s_time = Benchmark.measure {
      sync = a.any? &$slow_executor
    }
    parallel = nil
    p_time = Benchmark.measure {
      parallel = a.p_any? &$slow_executor
    }

    # because thread wait all splits
    assert_true(s_time.real < p_time.real)

    assert_equal(sync, parallel)
  end

  def test_map
    a = [*1..TEST_ARRAY_SIZE]
    sync = nil
    s_time = Benchmark.measure {
      sync = a.map &$slow_executor
    }
    parallel = nil
    p_time = Benchmark.measure {
      parallel = a.p_map &$slow_executor
    }

    assert_true(s_time.real > p_time.real)

    assert_equal(sync, parallel)
  end

  def test_select
    a = [*1..TEST_ARRAY_SIZE]
    sync = nil
    s_time = Benchmark.measure {
      sync = a.select &$slow_executor
    }
    parallel = nil
    p_time = Benchmark.measure {
      parallel = a.p_select &$slow_executor
    }

    assert_true(s_time.real > p_time.real)

    assert_equal(sync, parallel)
  end


end

