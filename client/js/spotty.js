var Channel, ChannelItemView, ChannelList, ChannelListItemView, ChannelListView, HomeView, IdentifyView, Member, MemberView, Track, TrackList, TrackListView, member, models, session, sp, templates, ui, views,
  __hasProp = Object.prototype.hasOwnProperty,
  __extends = function(child, parent) { for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; } function ctor() { this.constructor = child; } ctor.prototype = parent.prototype; child.prototype = new ctor; child.__super__ = parent.prototype; return child; };

sp = getSpotifyApi(1);

models = sp.require("sp://import/scripts/api/models");

views = sp.require("sp://import/scripts/api/views");

ui = sp.require("sp://import/scripts/ui");

session = models.session;

templates = {
  member_identify: '\
    <H1>hell0 <%= id %></H1>\
    <form class=".form-vertical">\
      <label>Name</label>\
      <input type="text" name="name"><br>\
      <label>Email</label>\
      <input type="text" name="email"><br>\
      <button class="btn btn-success identity-save">submit</button>\
    </form>',
  home: '<div class="row">\
      <div class="channel-header">\
        <a href="#" class="start-channel"><i class="icon-plus icon-white"> </i>Start your own channel</a>\
      </div>\
      <div class="span2" id="channel-list">\
      </div>\
      <div class="span8" id="channel-container">\
      </div>\
    </div',
  channel_list_item: '<span class="channel-list-item"><%= name %></span><i class="channel-add-songs icon-plus icon-white"></i>',
  channel_item: '<h1>welcome to radio <%= name%></h1>\
    <table class="track-list table-bordered table-striped">\
    </table>\
    <div>\
      What do you think about this channel?<br>\
      <textarea id="chatmsg" cols="80" rows="5"></textarea>\
      <div style="height: 300px; overflow:scroll;">\
        <table class="chat-list table-bordered table-striped">\
        </table>\
      </div>\
    </div>',
  track_list_item: '<tr><td><%= name %></td></tr>'
};

Member = (function(_super) {

  __extends(Member, _super);

  function Member() {
    Member.__super__.constructor.apply(this, arguments);
  }

  Member.prototype.url = function() {
    var base;
    return base = "/member/";
  };

  Member.prototype.sync = member_sync;

  Member.prototype.initialize = function() {
    this.set({
      id: session.anonymousUserID
    });
    return window.spotifyUserID = session.anonymousUserID;
  };

  return Member;

})(Backbone.Model);

MemberView = (function(_super) {

  __extends(MemberView, _super);

  function MemberView() {
    MemberView.__super__.constructor.apply(this, arguments);
  }

  MemberView.prototype.initialize = function() {
    return this.render();
  };

  MemberView.prototype.render = function() {
    var dict;
    dict = this.model.toJSON();
    return this.$el.html(this.template(dict));
  };

  return MemberView;

})(Backbone.View);

IdentifyView = (function(_super) {

  __extends(IdentifyView, _super);

  function IdentifyView() {
    IdentifyView.__super__.constructor.apply(this, arguments);
  }

  IdentifyView.prototype.events = {
    "click .identity-save": "save"
  };

  IdentifyView.prototype.save = function(e) {
    e.preventDefault();
    return this.model.save({
      name: (this.$el.find("input[name=name]")).val(),
      email: (this.$el.find("input[name=email]")).val()
    });
  };

  IdentifyView.prototype.template = _.template(templates.member_identify);

  return IdentifyView;

})(MemberView);

HomeView = (function(_super) {

  __extends(HomeView, _super);

  function HomeView() {
    HomeView.__super__.constructor.apply(this, arguments);
  }

  HomeView.prototype.template = _.template(templates.home);

  return HomeView;

})(MemberView);

ChannelListItemView = (function(_super) {

  __extends(ChannelListItemView, _super);

  function ChannelListItemView() {
    ChannelListItemView.__super__.constructor.apply(this, arguments);
  }

  ChannelListItemView.prototype.initialize = function() {
    return this.render();
  };

  ChannelListItemView.prototype.events = {
    "click .channel-list-item": "selectModel",
    "click .channel-add-songs": "addSongs"
  };

  ChannelListItemView.prototype.selectModel = function(event) {
    event.preventDefault();
    return this.model.set({
      selected: 1
    });
  };

  ChannelListItemView.prototype.addSongs = function(event) {
    var channelAddTrackView;
    event.preventDefault();
    return channelAddTrackView = new ChannelAddTrackView({
      model: this.model,
      el: "#channel-container"
    });
  };

  ChannelListItemView.prototype.template = _.template(templates.channel_list_item);

  ChannelListItemView.prototype.render = function() {
    this.$el.html(this.template(this.model.toJSON()));
    return this;
  };

  return ChannelListItemView;

})(Backbone.View);

ChannelListView = (function(_super) {

  __extends(ChannelListView, _super);

  function ChannelListView() {
    ChannelListView.__super__.constructor.apply(this, arguments);
  }

  ChannelListView.prototype.initialize = function() {
    this.collection.on("add", this.render, this);
    this.collection.on("reset", this.renderList, this);
    this.collection.on("change:selected", this.showChannel, this);
    return this.collection.fetch();
  };

  ChannelListView.prototype.channelItemElement = false;

  ChannelListView.prototype.renderList = function(list) {
    this.$el.children().remove();
    return list.each(function(model) {
      return this.render(model);
    }, this);
  };

  ChannelListView.prototype.showChannel = function(model, newValue) {
    var channelItemView;
    if (newValue === 0) return;
    channelItemView = new ChannelItemView({
      model: model,
      el: this.channelItemElement
    });
    channelItemView.render();
    return model.set({
      selected: 0
    });
  };

  ChannelListView.prototype.render = function(model) {
    var channelItemView, element;
    channelItemView = new ChannelListItemView({
      model: model
    });
    element = channelItemView.render().el;
    return this.$el.append(element);
  };

  return ChannelListView;

})(Backbone.View);

Channel = (function(_super) {

  __extends(Channel, _super);

  function Channel() {
    Channel.__super__.constructor.apply(this, arguments);
  }

  Channel.prototype.initialize = function() {
    return this.tracks = new TrackList();
  };

  Channel.prototype.url = function() {
    return "/channel/" + this.get("id") + "/listen";
  };

  Channel.prototype.tracks = false;

  Channel.prototype.listen = function() {
    var onSuccess;
    onSuccess = function() {
      var current, minutes, offset_ms, offset_sec, seconds, spotifyId, uri;
      this.tracks.reset(this.get("tracks"));
      current = this.get("current");
      spotifyId = current["current-id"];
      offset_ms = current["current-offset-ms"];
      offset_sec = Math.floor(offset_ms / 1000);
      minutes = Math.floor(offset_sec / 60);
      seconds = offset_sec % 60;
      uri = "spotify:track:" + spotifyId + "#" + minutes + ":" + seconds;
      console.log(uri);
      return models.player.play(uri);
    };
    onSuccess = _.bind(onSuccess, this);
    return this.fetch({
      success: onSuccess
    });
  };

  Channel.prototype.sync = list_sync;

  return Channel;

})(Backbone.Model);

ChannelItemView = (function(_super) {

  __extends(ChannelItemView, _super);

  function ChannelItemView() {
    ChannelItemView.__super__.constructor.apply(this, arguments);
  }

  ChannelItemView.prototype.template = _.template(templates.channel_item);

  ChannelItemView.prototype.events = {
    "keydown #chatmsg": "sendMessage"
  };

  ChannelItemView.prototype.sendMessage = function(event) {
    var message, msg;
    if (event.keyCode === 13) {
      msg = $.trim($(event.target).val());
      if (msg) {
        message = new ChatMessage({
          message: msg,
          channelId: this.model.get('id')
        });
        message.save();
        console.log("message saved");
        $(event.target).val("");
        event.preventDefault;
        return false;
      }
    }
  };

  ChannelItemView.prototype.render = function() {
    var chatMessages, chatMessagesView, trackListView;
    this.$el.html(this.template(this.model.toJSON()));
    this.model.listen();
    trackListView = new TrackListView({
      collection: this.model.tracks,
      el: this.$el.find(".track-list")
    });
    chatMessages = new ChatMessages({
      channel: this.model
    });
    return chatMessagesView = new ChatMessagesView({
      collection: chatMessages,
      el: this.$el.find(".chat-list")
    });
  };

  return ChannelItemView;

})(Backbone.View);

ChannelList = (function(_super) {

  __extends(ChannelList, _super);

  function ChannelList() {
    ChannelList.__super__.constructor.apply(this, arguments);
  }

  ChannelList.prototype.url = "/channels";

  ChannelList.prototype.model = Channel;

  ChannelList.prototype.sync = list_sync;

  return ChannelList;

})(Backbone.Collection);

Track = (function(_super) {

  __extends(Track, _super);

  function Track() {
    Track.__super__.constructor.apply(this, arguments);
  }

  return Track;

})(Backbone.Model);

TrackListView = (function(_super) {

  __extends(TrackListView, _super);

  function TrackListView() {
    TrackListView.__super__.constructor.apply(this, arguments);
  }

  TrackListView.prototype.initialize = function() {
    return this.collection.on("reset", this.render, this);
  };

  TrackListView.prototype.template = _.template(templates.track_list_item);

  TrackListView.prototype.render = function(tracks) {
    var iterator;
    this.$el.children().remove();
    iterator = _.bind(function(track) {
      var dict, spotifyId, spotifyTrack, uri;
      spotifyId = track.get("spotify-id");
      uri = "spotify:track:" + spotifyId;
      spotifyTrack = models.Track.fromURI(uri);
      dict = {
        name: spotifyTrack.name
      };
      return this.$el.append(this.template(dict));
    }, this);
    return tracks.each(iterator);
  };

  return TrackListView;

})(Backbone.View);

TrackList = (function(_super) {

  __extends(TrackList, _super);

  function TrackList() {
    TrackList.__super__.constructor.apply(this, arguments);
  }

  TrackList.prototype.model = Track;

  return TrackList;

})(Backbone.Collection);

member = new Member();

member.fetch({
  error: function(object, response) {
    var identifyView;
    return identifyView = new IdentifyView({
      el: "#page-content",
      model: member
    });
  },
  success: function() {
    var channelListView, homeView;
    if (member.has("email")) {
      homeView = new HomeView({
        el: "#page-content",
        model: member
      });
      window.channelList = new ChannelList();
      channelListView = new ChannelListView({
        collection: channelList,
        el: "#channel-list"
      });
      channelListView.channelItemElement = "#channel-container";
      ($(".start-channel")).on("click", function() {
        var channelCreateView;
        return channelCreateView = new ChannelCreateView({
          el: "#channel-container"
        });
      });
      return window.connection = new ChatConnection();
    } else {
      return console.error("cannot be here without email address");
    }
  }
});
