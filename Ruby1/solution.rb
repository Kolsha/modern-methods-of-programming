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
      res += res.select { |r| (r[-1] != char) && r.length < n && !res.include?(r + char) }.reduce([]) { |new_res, r| new_res << (r + char) }

      #res = new_res
    }

    res = res[n + 1..-1]

  end

  res = res.select { |el| el.length == n }
  return res.sort
end

def reduce_sol(str, n)

  return [] if n < 1

  Array.new(n, str.chars.to_a.uniq).reduce([[]]) { |acc, el|

    acc.map { |a|
      el.select { |e| a.last == nil || a.last[0] != e }.map { |e|

        (a + [e])
      }
    }.reduce([]) { |a, e| a + e }

  }.map { |e| e.join('') }
end

# arr = []
#
# # puts arr[-1] != 1
# n = arr + [1]
# puts arr, n


reduce_sol('abc', 10).each do |e|
  puts
  puts e
end


