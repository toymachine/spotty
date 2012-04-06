// Generated by CoffeeScript 1.2.1-pre
var ChatConnection, ChatMessages, ChatMessagesView, connection,
  __hasProp = {}.hasOwnProperty,
  __extends = function(child, parent) { for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; } function ctor() { this.constructor = child; } ctor.prototype = parent.prototype; child.prototype = new ctor; child.__super__ = parent.prototype; return child; };

ChatConnection = (function(_super) {

  __extends(ChatConnection, _super);

  ChatConnection.name = 'ChatConnection';

  function ChatConnection() {
    return ChatConnection.__super__.constructor.apply(this, arguments);
  }

  ChatConnection.prototype.sync = list_sync;

  ChatConnection.prototype.url = "/chat/token";

  ChatConnection.prototype.initialize = function() {
    var onClose, onError, onMessage, onOpen;
    onOpen = function() {
      return console.log("open chat channel");
    };
    onMessage = function() {
      return console.log("message", arguments);
    };
    onError = function() {
      return console.log("error", arguments);
    };
    onClose = function() {
      return console.log("closed");
    };
    return this.fetch({
      success: function(chatConnection) {
        var rtChannel, socket, token;
        token = chatConnection.get("token");
        return;
        rtChannel = new goog.appengine.Channel(token);
        socket = rtChannel.open();
        socket.onopen = onOpen;
        socket.onmessage = onMessage;
        socket.onerror = onError;
        return socket.onclose = onClose;
      }
    });
  };

  return ChatConnection;

})(Backbone.Model);

connection = new ChatConnection();

ChatMessages = (function(_super) {

  __extends(ChatMessages, _super);

  ChatMessages.name = 'ChatMessages';

  function ChatMessages() {
    return ChatMessages.__super__.constructor.apply(this, arguments);
  }

  ChatMessages.prototype.sync = list_sync;

  ChatMessages.prototype.channel = false;

  ChatMessages.prototype.initialize = function(options) {
    return this.channel = options.channel;
  };

  ChatMessages.prototype.url = function() {
    var channelId;
    channelId = this.channel.get("id");
    return "/channel/" + channelId + "/chat-messages";
  };

  return ChatMessages;

})(Backbone.Collection);

ChatMessagesView = (function(_super) {

  __extends(ChatMessagesView, _super);

  ChatMessagesView.name = 'ChatMessagesView';

  function ChatMessagesView() {
    return ChatMessagesView.__super__.constructor.apply(this, arguments);
  }

  ChatMessagesView.prototype.initialize = function() {
    this.collection.on("reset", this.renderList, this);
    return this.collection.fetch();
  };

  ChatMessagesView.prototype.renderList = function(list) {
    return list.each(function(message) {
      console.log(message);
      return this.$el.append(message);
    }, this);
  };

  return ChatMessagesView;

})(Backbone.View);
