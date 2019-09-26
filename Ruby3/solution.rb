#
# http://nginx.org/en/docs/http/ngx_http_log_module.html
# http://nginx.org/en/docs/http/ngx_http_core_module.html#internal
#
# https://www.rubydoc.info/github/immutable-ruby/immutable-ruby/Immutable/Deque
#
#
# log_format compression '$remote_addr - $remote_user [$time_local] '
#                        '"$request" $status $bytes_sent '
require 'time'
require "immutable/deque"


str = '66.249.66.139 - - [01/Sep/2019:01:49:22 +0300] "GET / HTTP/1.0" 302 -'

def nginx_date_to_timestamp(str)
  DateTime.strptime(str, "%d/%b/%Y:%H:%M:%S %Z").to_time.to_i
end

def regexp_mapper(str)
  regexp = /^
          (\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3})      # remote_addr
          \s-                                       # simple -
          \s(\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}|-)  # remote_user
          \s\[(.+?)\]                               # time_local
          \s"(\w{3,10})\s(.+?)\s.+?                 # method path
          \s(\d+)                                   #	status
          \s(\d+|-)                                 #	bytes_sent
          /x
  m = str.match(regexp)
  if m then
    res = Hash.new
    res['ip'],
        res['remote_user'],
        res['timestamp'],
        res['method'],
        res['path'],
        res['status'],
        res['bytes'] = m.captures
    #may be to utc
    res['timestamp'] = nginx_date_to_timestamp(res['timestamp'])
    return res
  end
  nil
end

class RobotStatistic
  @@deque = Immutable::Deque.empty
  @@user_stat = Hash.new
  @@robot_count = 0

  # @param [Integer] threshold
  # @param [Integer] time_win in seconds
  def initialize(threshold = 30, time_win = 300_000)
    @threshold = threshold
    @time_win = time_win
  end

  # @param [Ip string] user_id
  # @param [Integer] timestamp
  def insert(user_id, timestamp)
    remove_before(timestamp)


    @@deque = @@deque.push([timestamp, user_id])

    @@user_stat[user_id] = (!@@user_stat[user_id]) ? 0 : @@user_stat[user_id]


    prev_stat = @@user_stat[user_id]
    new_stat = @@user_stat[user_id] + 1

    if prev_stat < @threshold && new_stat >= @threshold
      @@robot_count += 1
    end
    @@user_stat[user_id] = new_stat
  end

  # @param [Integer] timestamp
  # @return [Integer]
  def get_robot_count(timestamp = Time.now.to_i)
    remove_before(timestamp)
    @@robot_count
  end

  def get_robots
    @@user_stat.select { |key, value| value >= @threshold }
  end

  def user_stat
    @@user_stat
  end

  def deque
    @@deque
  end

  private

  def dec_user_stat(user_id)
    # if !@@user_stat[user_id]
    #   @@user_stat[user_id] = 0
    #   return
    # end

    prev_stat = @@user_stat[user_id]
    new_stat = [0, @@user_stat[user_id] - 1].max

    # puts "#{prev_stat}  -  #{new_stat}"

    if prev_stat >= @threshold && new_stat < @threshold
      @@robot_count = [0, @@robot_count - 1].max
      # puts "r_cnt = #{@@robot_count}"
    end

    @@user_stat[user_id] = new_stat
  end

  def remove_before(timestamp)
    if !@@deque.empty?
      item_timestamp = @@deque.first.first + @time_win
      item_user_id = @@deque.first.last
      # puts item_user_id, item_timestamp

      if item_timestamp < timestamp
        dec_user_stat(item_user_id)

        @@deque = @@deque.shift
        remove_before(timestamp)
      end


    end
  end
end

robot_stat = RobotStatistic.new

robot_count = robot_stat.get_robot_count
File.open(File.join(File.dirname(__FILE__), 'ksware.ru_access.log')).readlines.each {
    |line|
  req = regexp_mapper line
  if req
    robot_stat.insert(req['ip'], req['timestamp'])
    cur_robot_count = robot_stat.get_robot_count(req['timestamp'])
    if robot_count != cur_robot_count
      puts "robot count changed to #{cur_robot_count}"
      robot_count = cur_robot_count

      puts robot_stat.get_robots
    end
  end


}

# robot_stat.user_stat.each { |x| puts x if x.last > 30 }
# puts robot_stat.deque
# puts regexp_mapper str


