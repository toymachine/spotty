sp = getSpotifyApi 1
models = sp.require "sp://import/scripts/api/models"
views = sp.require "sp://import/scripts/api/views"
ui = sp.require "sp://import/scripts/ui"
session = models.session

templates =
  member_identify: '
    hell0 <%= id %>
    <form>
      <input type="text" name="name"><br>
      <input type="text" name="email"><br>
      <button class="identity-save">submit</button>
    </form>'
  home: 'hello <%= name %>
    <div id="channel-list">
    </div>
    <div id="channel-container">
    </div>'
  channel_list_item: '<%= name %>'

sync = (method, model, options) ->
    options.url = "http://127.0.0.1:8080/api" + model.url()
    if method is "read"
      options.url = options.url + model.get "id"
    Backbone.sync method, model, options

class Member extends Backbone.Model
  url: () ->
    base = "/member/"
  sync: sync
  initialize: () ->
      @set {id: session.anonymousUserID}

class MemberView extends Backbone.View
  initialize: () ->
    @render()
  render: () ->
    dict = @model.toJSON()
    @$el.html @template dict

class IdentifyView extends MemberView
  events:
    "click .identity-save": "save"
  save: (e) ->
    e.preventDefault()
    @model.save
      name: (@$el.find "input[name=name]").val()
      email: (@$el.find "input[name=email]").val()
  template: _.template templates.member_identify

class HomeView extends MemberView
  template: _.template templates.home

class ChannelListItemView extends Backbone.View
  initialize: () ->
    @render()
  template: _.template templates.channel_list_item
  render: () ->
    @$el.html @template @model.toJSON()
    @

class ChannelListView extends Backbone.View
  initialize: () ->
    @collection.on "add", @render, @
    @collection.on "reset", @renderList, @
    @collection.fetch()
  renderList: (list) ->
    @$el.children().remove()
    list.each (model) ->
      @render(model)
    , @
  render: (model) ->
    channelItemView = new ChannelListItemView {model: model}
    element = channelItemView.render().el
    @$el.append element

class Channel extends Backbone.Model

class ChannelList extends Backbone.Collection
  model: Channel

member = new Member()
member.fetch()
console.log member.toJSON()
if member.has("email")
  homeView = new HomeView {el: "#page-content", model: member}
  channelList = new ChannelList()
  channelList.fetch()
  channelListView = new ChannelListView {collection: channelList, el: "#channel-list"}
else
  identifyView = new IdentifyView {el: "#page-content", model: member}
