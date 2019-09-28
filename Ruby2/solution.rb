#
# в текущей реализации inject не возможен / не имеет смысла
# поправить
#
class Array
  THREAD_COUNT = 4
  private_constant :THREAD_COUNT

  private

  def parallel_common(method, &func)
    slice_len = self.length / THREAD_COUNT
    slice_len = (length % THREAD_COUNT) ? slice_len + 1 : slice_len
    puts method
    thread_pool = Array.new(THREAD_COUNT) do
    |idx|
      Thread.new(self[idx * slice_len, slice_len]) {
          |slice|


        if slice
          res = slice.send(method, &func)
        else
          res = []
        end

        res
      }
    end

    thread_pool.reduce([]) { |output, thread| output << thread.value }
  end

  def p_map(&func)
    parallel_common("map", &func).flatten
  end

  def p_all?(&func)
    res = parallel_common("all?", &func)
    # puts "#{res}" if @debug
    res.all? { |x| x }
  end

  def p_any?(&func)
    res = parallel_common("any?", &func)
    res.any? { |x| x }
  end

  def p_select(&func)
    parallel_common("select", &func).flatten
    # zipped = self.zip(res)
    # # puts res
    # # puts zipped
    # zipped
    #     .select { |x| x[1] }
    #     .map { |x| x[0] }
  end

end

