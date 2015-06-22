require 'eventmachine'
require 'em-websocket'
require 'socket'


class Client
  attr_accessor :websocket
  def initialize(websocket_arg)
    @websocket = websocket_arg
  end
end


class ChatRoom

  def initialize
    @clients = []

    @server = TCPServer.new 9000
  end

  def start
    EventMachine.run {
      EventMachine::WebSocket.start(:host => "0.0.0.0", :port => 8080) do |ws|

        ws.onopen { |websocket|
          puts "WebSocket connection open"
          #puts @clients.inspect
          @client = @server.accept    # Wait for a client to connect
        }

        ws.onmessage { |msg|
          puts msg
          client.puts msg
          ws.send(msg)
        }

        ws.onclose {
          puts "WebSocket connection closed"
        }
      end
    }
  end

end
