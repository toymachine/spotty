var ChannelAddTrackView, ChannelCreateView, MyChannel, TrackItem, TrackItemView, TrackSearchList, TrackSearchListView, models, sp, templates,
  __hasProp = Object.prototype.hasOwnProperty,
  __extends = function(child, parent) { for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; } function ctor() { this.constructor = child; } ctor.prototype = parent.prototype; child.prototype = new ctor; child.__super__ = parent.prototype; return child; };

sp = getSpotifyApi(1);

models = sp.require("sp://import/scripts/api/models");

templates = {
  channel_create: '<form class="well .form-vertical">\
      <label>Name</label>\
      <input type="text" name="name">\
      <label>Description</label>\
      <input type="text" name="description">\
      <label>Image url</label>\
      <input type="text" name="imageurl">\
      <button class="btn btn-success identity-save">submit</button>\
    </form>',
  channel_add_track: '<h1>Add tracks to  <%= name %></h1>\
    <form class="well form-search">\
      <input type="text" class="input-medium search-query">\
      <button type="submit" class="btn">Search</button>\
    </form>\
    <div class="track-search-result"></div>',
  track_item: 'test'
};

MyChannel = (function(_super) {

  __extends(MyChannel, _super);

  function MyChannel() {
    MyChannel.__super__.constructor.apply(this, arguments);
  }

  MyChannel.prototype.url = "/channel";

  MyChannel.prototype.sync = list_sync;

  return MyChannel;

})(Backbone.Model);

TrackSearchList = (function(_super) {

  __extends(TrackSearchList, _super);

  function TrackSearchList() {
    TrackSearchList.__super__.constructor.apply(this, arguments);
  }

  TrackSearchList.prototype.model = TrackItem;

  return TrackSearchList;

})(Backbone.Collection);

TrackSearchListView = (function(_super) {

  __extends(TrackSearchListView, _super);

  function TrackSearchListView() {
    TrackSearchListView.__super__.constructor.apply(this, arguments);
  }

  TrackSearchListView.prototype.initialize = function() {
    return this.collection.on("reset", this.render, this);
  };

  TrackSearchListView.prototype.render = function(list) {
    console.log(this.$el);
    return list.each(function(track) {
      var trackItemView;
      trackItemView = new TrackItemView({
        model: track
      });
      return this.$el.append(trackItemView.render());
    }, this);
  };

  return TrackSearchListView;

})(Backbone.View);

TrackItem = (function(_super) {

  __extends(TrackItem, _super);

  function TrackItem() {
    TrackItem.__super__.constructor.apply(this, arguments);
  }

  return TrackItem;

})(Backbone.Model);

TrackItemView = (function(_super) {

  __extends(TrackItemView, _super);

  function TrackItemView() {
    TrackItemView.__super__.constructor.apply(this, arguments);
  }

  TrackItemView.prototype.template = _.template(templates.track_item);

  TrackItemView.prototype.render = function() {
    return this.$el.html(this.template(this.model.toJSON()));
  };

  return TrackItemView;

})(Backbone.View);

ChannelAddTrackView = (function(_super) {

  __extends(ChannelAddTrackView, _super);

  function ChannelAddTrackView() {
    ChannelAddTrackView.__super__.constructor.apply(this, arguments);
  }

  ChannelAddTrackView.prototype.initialize = function() {
    this.render();
    this.trackList = new TrackSearchList();
    return this.trackListView = new TrackSearchListView({
      collection: this.trackList
    });
  };

  ChannelAddTrackView.prototype.events = {
    "keyup .search-query": "search"
  };

  ChannelAddTrackView.prototype.search = function(event) {
    var query, search, tempList;
    if (event.keyCode !== 13) return;
    query = ($(event.target)).val();
    sp = getSpotifyApi(1);
    models = sp.require("sp://import/scripts/api/models");
    search = new models.Search(query);
    search.localResults = models.LOCALSEARCHRESULTS.IGNORE;
    tempList = [];
    search.observe(models.EVENT.CHANGE, function() {
      if (search.tracks.length > 0) {
        $.each(search.tracks, function(index, track) {
          track = new TrackItem(JSON.stringify(track));
          return tempList.push(track);
        });
        console.log(tempList);
        return this.trackList.reset(tempList);
      } else {
        return alert("not tracks found for " + query);
      }
    });
    return search.appendNext();
  };

  ChannelAddTrackView.prototype.template = _.template(templates.channel_add_track);

  ChannelAddTrackView.prototype.render = function() {
    return this.$el.html(this.template(this.model.toJSON()));
  };

  return ChannelAddTrackView;

})(Backbone.View);

ChannelCreateView = (function(_super) {

  __extends(ChannelCreateView, _super);

  function ChannelCreateView() {
    ChannelCreateView.__super__.constructor.apply(this, arguments);
  }

  ChannelCreateView.prototype.initialize = function() {
    return this.render();
  };

  ChannelCreateView.prototype.template = _.template(templates.channel_create);

  ChannelCreateView.prototype.render = function() {
    return this.$el.html(this.template);
  };

  ChannelCreateView.prototype.events = {
    "click button": "submit"
  };

  ChannelCreateView.prototype.submit = function(e) {
    var channel, channelAddTrackView, data, form;
    e.preventDefault();
    form = this.$el.find("form");
    data = {
      name: form.find("[name=name]").val(),
      description: form.find("[name=description]").val(),
      imageurl: form.find("[name=imageurl]").val()
    };
    console.log(data);
    channel = new MyChannel(data);
    channel.save();
    window.channelList.add(new Channel(channel.toJSON()));
    return channelAddTrackView = new ChannelAddTrackView({
      model: channel,
      el: this.el
    });
  };

  return ChannelCreateView;

})(Backbone.View);
