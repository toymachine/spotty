var ChatConnection, ChatMessage, ChatMessages, ChatMessagesView,
  __hasProp = Object.prototype.hasOwnProperty,
  __extends = function(child, parent) { for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; } function ctor() { this.constructor = child; } ctor.prototype = parent.prototype; child.prototype = new ctor; child.__super__ = parent.prototype; return child; };

ChatConnection = (function(_super) {

  __extends(ChatConnection, _super);

  function ChatConnection() {
    ChatConnection.__super__.constructor.apply(this, arguments);
  }

  ChatConnection.prototype.sync = list_sync;

  ChatConnection.prototype.url = "/chat/token";

  ChatConnection.prototype.initialize = function() {
    var onClose, onError, onMessage, onOpen;
    onOpen = function() {
      return console.log("open chat channel");
    };
    onMessage = function(arg) {
      var channelId, channelMessages, chatMessage, data, msg;
      data = $.parseJSON(arg.data);
      msg = data["msg"];
      channelId = data["channel-id"];
      channelMessages = window.channel_messages[channelId];
      if (msg && channelId && channelMessages) {
        chatMessage = new ChatMessage({
          msg: msg,
          channelId: channelId
        });
        return channelMessages.add(chatMessage);
      }
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

ChatMessages = (function(_super) {

  __extends(ChatMessages, _super);

  function ChatMessages() {
    ChatMessages.__super__.constructor.apply(this, arguments);
  }

  ChatMessages.prototype.sync = list_sync;

  ChatMessages.prototype.channel = false;

  ChatMessages.prototype.initialize = function(options) {
    this.channel = options.channel;
    window.channel_messages = window.channel_messages || {};
    return window.channel_messages[this.channel.get('id')] = this;
  };

  ChatMessages.prototype.url = function() {
    var channelId;
    channelId = this.channel.get("id");
    return "/channel/" + channelId + "/chat-messages";
  };

  return ChatMessages;

})(Backbone.Collection);

ChatMessage = (function(_super) {

  __extends(ChatMessage, _super);

  function ChatMessage() {
    ChatMessage.__super__.constructor.apply(this, arguments);
  }

  ChatMessage.prototype.sync = list_sync;

  ChatMessage.prototype.url = function() {
    var channelId;
    channelId = this.get("channelId");
    return "/channel/" + channelId + "/chat-message";
  };

  return ChatMessage;

})(Backbone.Model);

ChatMessagesView = (function(_super) {

  __extends(ChatMessagesView, _super);

  function ChatMessagesView() {
    ChatMessagesView.__super__.constructor.apply(this, arguments);
  }

  ChatMessagesView.prototype.initialize = function() {
    this.collection.on("reset", this.renderList, this);
    this.collection.on("add", this.render, this);
    return this.collection.fetch();
  };

  ChatMessagesView.prototype.render = function(message) {
    return this.$el.append("<tr>" + "<td><img src='" + message.get('avatar') + "'></td>" + "<td>" + message.get('msg') + "</td>" + "</tr>");
  };

  ChatMessagesView.prototype.renderList = function(list) {
    return list.each(function(message) {
      return this.render(message);
    }, this);
  };

  return ChatMessagesView;

})(Backbone.View);
