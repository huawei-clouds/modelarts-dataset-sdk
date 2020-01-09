# Copyright 2018 Deep Learning Service of Huawei Cloud. All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

""" Function for Python 2 vs .3 compatibility.
## Conversion routines
In addition to the functions below, 'as_str' converts an object to a 'str'.

@@ as_bytes
@@ as_text

"""

from __future__ import absolute_import
from __future__ import division
from __future__ import print_function

import six as _six


def as_bytes(bytes_or_text, encoding='utf-8'):
    """Convert either bytes or unicode to 'bytes', using utf-8 encoding for text.

    :arg:
    bytes_or_text: A 'bytes', 'str', or 'unicode' object.
    encoding: A string indicating the charset for encoding unicode.
    :return:
    A 'str' object (python 2).
    :raise:
    TypeError: If 'bytes_or_text' is not a binary or unicode string.
    """
    if isinstance(bytes_or_text, _six.text_type):
        return bytes_or_text.encode(encoding)
    elif isinstance(bytes_or_text, bytes):
        return bytes_or_text
    else:
        raise TypeError('Expected binary or unicode string, got %r' % bytes_or_text)


def as_text(bytes_or_text, encoding='utf-8'):
    """Return the given argument as a unicode string.

    :arg:
    bytes_or_text: A 'bytes', 'str', or 'unicode' object.
    encoding: A string indicating the charset for encoding unicode.
    :return:
    A 'unicode' (Ptyhon 2) or 'str' (Python 3) object.
    :raise:
    TypeError: If 'bytes_or_text' is not a binary or unicode string.
    """
    if isinstance(bytes_or_text, _six.text_type):
        return bytes_or_text
    elif isinstance(bytes_or_text, bytes):
        return bytes_or_text.decode(encoding)
    else:
        raise TypeError('Excepted binary or unicode string, got %r' % bytes_or_text)


# Convert an object to a 'str' in both Python 2 and 3
if _six.PY2:
    as_str = as_bytes
else:
    as_str = as_text
