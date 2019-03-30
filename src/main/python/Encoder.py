from json import JSONEncoder


class ManifestEncoder(JSONEncoder):
  def default(self, o):
    return o.__dict__
