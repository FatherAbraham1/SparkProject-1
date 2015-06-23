require File.expand_path('../boot', __FILE__)


require 'rails/all'
# Require the gems listed in Gemfile, including any gems
# you've limited to :test, :development, or :production.
Bundler.require(*Rails.groups)

module SparkProject

  class Client
    attr_accessor :websocket
    def initialize(websocket_arg)
      @websocket = websocket_arg
    end
  end


  class ChatRoom

    def initialize
    end

    def start
      EventMachine.run {
        EventMachine::WebSocket.start(:host => "0.0.0.0", :port => 8080) do |ws|

          ws.onopen { |websocket|
            puts "WebSocket connection open"
            @client = TCPSocket.new 'localhost', 9000             # connect to Java Spark Driver to send method name

            Thread.start do
              @server = TCPServer.new 9003                        # listens for spark driver connection to send/receive file data
              @file_client_input = @server.accept                 # This is the socket where the procesed data will come from.
              puts "connection open with input spark file stream"
              @file_client_output = @server.accept                # This is the apache spark socket connector.
              puts "connection open with output spark file stream"
            end
          }

          ws.onmessage { |msg|
            puts msg
            if(msg.start_with?('method::') )
              msg.sub!("method::", '')
              Thread.start do
                puts "starting thread"
                while true
                  @line = @file_client_input.gets
                  puts @line
                  ws.send(@line)
                end
              end
              @client.puts msg
            else
              @file_client_output.puts msg
            end

            #ws.send(msg)
          }

          ws.onclose {
            puts "WebSocket connection closed"
          }
        end
      }
    end
  end

class Application < Rails::Application
    # Settings in config/environments/* take precedence over those specified here.
    # Application configuration should go into files in config/initializers
    # -- all .rb files in that directory are automatically loaded.

    # Set Time.zone default to the specified zone and make Active Record auto-convert to this zone.
    # Run "rake -D time" for a list of tasks for finding time zone names. Default is UTC.
    # config.time_zone = 'Central Time (US & Canada)'

    # The default locale is :en and all translations from config/locales/*.rb,yml are auto loaded.
    # config.i18n.load_path += Dir[Rails.root.join('my', 'locales', '*.{rb,yml}').to_s]
    # config.i18n.default_locale = :de

    # Do not swallow errors in after_commit/after_rollback callbacks.
    config.active_record.raise_in_transactional_callbacks = true

    config.after_initialize do
      Thread.start do
        chatroom = ChatRoom.new
        chatroom.start
      end
    end
  end
end
