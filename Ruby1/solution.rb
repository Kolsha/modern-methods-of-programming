require "test/unit"

def recursive_solution(start, arr, max_len)
  arr = arr.uniq
  result = []
  if max_len < 1 || arr.empty?
    return result
  end

  if start.length < max_len
    arr.select { |el| el != start[-1] }.each do |el|
      result << recursive_solution(start + el, arr, max_len)
    end
  else
    return start
  end

  return result.flatten.sort

end


puts recursive_solution('', %w(a b c), 0)


# puts recursive_solution('', str, n)


def n_times_sol(str, n)
  str = str.uniq
  if n < 1 || str.empty?
    return []
  end
  res = str.dup

  (n - 1).times do

    str.map { |char|
      #new_res = res

      # reduce acc = array
      # res.select { |r| (r[-1] != char) && r.length < n }.map { |r| new_res << r + char }
      res += res.select { |r| (r[-1] != char) && r.length < n }.reduce([]){ |new_res, r| new_res << ( r + char)}

      #res = new_res
    }

    res = res[n + 1..-1]

  end
# res = res.select{|el| el.length == n}
  return res.sort
end


# puts n_times_sol(str, n)


# str.map do |c|
#   perm = c
#   (n-1).times{
#     str.select{|n| c!=n}.map {|n| perm += n}
#   }
#   puts perm
# end
#
# n.times do
#   puts str
# end

# puts str.reduce(''){|f, i| f + i}


