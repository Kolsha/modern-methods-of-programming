#
# в текущей реализации inject не возможен / не имеет смысла
#
class Array
  THREAD_COUNT = 4
  private_constant :THREAD_COUNT

  #private
  def parallel_common(&func)
    slice_len = self.length / THREAD_COUNT
    slice_len = (length % THREAD_COUNT) ? slice_len + 1 : slice_len

    thread_pool = Array.new(THREAD_COUNT) do
    |idx|
      Thread.new(self[idx * slice_len, slice_len]) {
          |slice|


        if slice
          res = slice.map &func
        else
          res = []
        end

        res
      }
    end

    thread_pool.reduce([]) { |output, thread| output += thread.value }
  end

  def p_map(&func)
    parallel_common &func
  end

  def p_all?(&func)
    res = parallel_common &func
    # puts "#{res}" if @debug
    res.all? { |x| x }
  end

  def p_any?(&func)
    res = parallel_common &func
    res.any? { |x| x }
  end

  def p_select(&func)
    res = parallel_common &func
    zipped = self.zip(res)
    # puts res
    # puts zipped
    zipped
        .select { |x| x[1] }
        .map { |x| x[0] }
  end

end



