package org.s1ck.ldbc;

/**
 * Copyright 2015 martin.
 */
public class LDBCConstants {

  public static final String FILENAME_TOKEN_DELIMITER = "_";

  public static final String FIELD_DELIMITER = "\\|";

  public static final String TYPE_DISCRIMINATOR_FIELD = "type";

  public static final String VERTEX_ID_FIELD = "id";

  /**
   * Field types
   */

  public enum FieldType {
    INT,
    LONG,
    STRING,
    DATE,
    DATETIME
  }

  /**
   * Vertex classes
   */

  public static final String VERTEX_CLASS_COMMENT = "comment";
  public static final String VERTEX_CLASS_PLACE = "place";
  public static final String VERTEX_CLASS_TAG = "tag";
  public static final String VERTEX_CLASS_TAGCLASS = "tagclass";
  public static final String VERTEX_CLASS_FORUM = "forum";
  public static final String VERTEX_CLASS_PERSON = "person";
  public static final String VERTEX_CLASS_ORGANISATION = "organisation";
  public static final String VERTEX_CLASS_POST = "post";

  /**
   * Edge classes
   */

  public static final String EDGE_CLASS_KNOWS = "knows";
  public static final String EDGE_CLASS_HAS_TYPE = "hasType";
  public static final String EDGE_CLASS_IS_LOCATED_IN = "isLocatedIn";
  public static final String EDGE_CLASS_HAS_INTEREST = "hasInterest";
  public static final String EDGE_CLASS_REPLY_OF = "replyOf";
  public static final String EDGE_CLASS_STUDY_AT = "studyAt";
  public static final String EDGE_CLASS_HAS_MODERATOR = "hasModerator";
  public static final String EDGE_CLASS_HAS_MEMBER = "hasMember";
  public static final String EDGE_CLASS_HAS_TAG = "hasTag";
  public static final String EDGE_CLASS_HAS_CREATOR = "hasCreator";
  public static final String EDGE_CLASS_WORK_AT = "workAt";
  public static final String EDGE_CLASS_CONTAINER_OF = "containerOf";
  public static final String EDGE_CLASS_IS_PART_OF = "isPartOf";
  public static final String EDGE_CLASS_IS_SUBCLASS_OF = "isSubclassOf";
  public static final String EDGE_CLASS_LIKES = "likes";

  /**
   * Property classes
   */

  public static final String PROPERTY_CLASS_EMAIL = "email";
  public static final String PROPERTY_CLASS_SPEAKS = "speaks";

  /**
   * Vertex class fields
   */

  public static final String[] VERTEX_CLASS_COMMENT_FIELDS = new String[]{
    VERTEX_ID_FIELD,
    "creationDate",
    "locationIP",
    "browserUsed",
    "content",
    "length"
  };
  public static final String[] VERTEX_CLASS_PLACE_FIELDS = new String[]{
    VERTEX_ID_FIELD,
    "name",
    "url",
    "type"
  };
  public static final String[] VERTEX_CLASS_TAG_FIELDS = new String[]{
    VERTEX_ID_FIELD,
    "name",
    "url"
  };
  public static final String[] VERTEX_CLASS_TAGCLASS_FIELDS = new String[]{
    VERTEX_ID_FIELD,
    "name",
    "url"
  };
  public static final String[] VERTEX_CLASS_FORUM_FIELDS = new String[]{
    VERTEX_ID_FIELD,
    "title",
    "creationDate"
  };
  public static final String[] VERTEX_CLASS_PERSON_FIELDS = new String[]{
    VERTEX_ID_FIELD,
    "firstName",
    "lastName",
    "gender",
    "birthday",
    "creationDate",
    "locationIP",
    "browserUsed"
  };
  public static final String[] VERTEX_CLASS_ORGANISATION_FIELDS = new String[]{
    VERTEX_ID_FIELD,
    "type",
    "name",
    "url"
  };
  public static final String[] VERTEX_CLASS_POST_FIELDS = new String[]{
    VERTEX_ID_FIELD,
    "imageFile",
    "creationDate",
    "locationIP",
    "browserUsed",
    "language",
    "content",
    "length"
  };

  /**
   * Edge class fields
   */

  public static final String[] EDGE_CLASS_KNOWS_FIELDS = new String[]{
    VERTEX_ID_FIELD,
    VERTEX_ID_FIELD,
    "creationDate"
  };

  public static final String[] EDGE_CLASS_HAS_TYPE_FIELDS = new String[]{
    VERTEX_ID_FIELD,
    VERTEX_ID_FIELD,
  };

  public static final String[] EDGE_CLASS_IS_LOCATED_IN_FIELDS = new String[]{
    VERTEX_ID_FIELD,
    VERTEX_ID_FIELD,
  };

  public static final String[] EDGE_CLASS_HAS_INTEREST_FIELDS = new String[]{
    VERTEX_ID_FIELD,
    VERTEX_ID_FIELD,
  };

  public static final String[] EDGE_CLASS_REPLY_OF_FIELDS = new String[]{
    VERTEX_ID_FIELD,
    VERTEX_ID_FIELD,
  };

  public static final String[] EDGE_CLASS_STUDY_AT_FIELDS = new String[]{
    VERTEX_ID_FIELD,
    VERTEX_ID_FIELD,
    "classYear"
  };

  public static final String[] EDGE_CLASS_HAS_MODERATOR_FIELDS = new String[]{
    VERTEX_ID_FIELD,
    VERTEX_ID_FIELD,
  };

  public static final String[] EDGE_CLASS_HAS_MEMBER_FIELDS = new String[]{
    VERTEX_ID_FIELD,
    VERTEX_ID_FIELD,
    "joinDate"
  };

  public static final String[] EDGE_CLASS_HAS_TAG_FIELDS = new String[]{
    VERTEX_ID_FIELD,
    VERTEX_ID_FIELD,
  };

  public static final String[] EDGE_CLASS_HAS_CREATOR_FIELDS = new String[]{
    VERTEX_ID_FIELD,
    VERTEX_ID_FIELD,
  };

  public static final String[] EDGE_CLASS_WORK_AT_FIELDS = new String[]{
    VERTEX_ID_FIELD,
    VERTEX_ID_FIELD,
    "workFrom"
  };

  public static final String[] EDGE_CLASS_CONTAINER_OF_FIELDS = new String[]{
    VERTEX_ID_FIELD,
    VERTEX_ID_FIELD,
  };

  public static final String[] EDGE_CLASS_IS_PART_OF_FIELDS = new String[]{
    VERTEX_ID_FIELD,
    VERTEX_ID_FIELD,
  };

  public static final String[] EDGE_CLASS_IS_SUBCLASS_OF_FIELDS = new String[]{
    VERTEX_ID_FIELD,
    VERTEX_ID_FIELD,
  };

  public static final String[] EDGE_CLASS_LIKES_FIELDS = new String[]{
    VERTEX_ID_FIELD,
    VERTEX_ID_FIELD,
    "creationDate"
  };

  /**
   * Property class fields
   */

  public static final String[] PROPERTY_CLASS_EMAIL_FIELDS = new String[]{
    VERTEX_ID_FIELD,
    "email"
  };

  public static final String[] PROPERTY_CLASS_SPEAKS_FIELDS = new String[]{
    VERTEX_ID_FIELD,
    "language"
  };

  /**
   * Vertex class field types
   */

  public static final FieldType[] VERTEX_CLASS_COMMENT_FIELD_TYPES =
    new FieldType[]{
      // id
      FieldType.LONG,
      // creationDate
      FieldType.DATETIME,
      // locationIP
      FieldType.STRING,
      // browserUsed
      FieldType.STRING,
      // content
      FieldType.STRING,
      // length
      FieldType.INT
    };

  public static final FieldType[] VERTEX_CLASS_PLACE_FIELD_TYPES =
    new FieldType[]{
      // id
      FieldType.LONG,
      // name
      FieldType.STRING,
      // url
      FieldType.STRING,
      // type
      FieldType.STRING
    };

  public static final FieldType[] VERTEX_CLASS_TAG_FIELD_TYPES =
    new FieldType[]{
      // id
      FieldType.LONG,
      // name
      FieldType.STRING,
      // url
      FieldType.STRING
    };

  public static final FieldType[] VERTEX_CLASS_TAGCLASS_FIELD_TYPES =
    new FieldType[]{
      // id
      FieldType.LONG,
      // name
      FieldType.STRING,
      // url
      FieldType.STRING
    };

  public static final FieldType[] VERTEX_CLASS_FORUM_FIELD_TYPES =
    new FieldType[]{
      // id
      FieldType.LONG,
      // title
      FieldType.STRING,
      // creationDate
      FieldType.DATETIME
    };

  public static final FieldType[] VERTEX_CLASS_PERSON_FIELD_TYPES =
    new FieldType[]{
      // id
      FieldType.LONG,
      // firstName
      FieldType.STRING,
      // lastName
      FieldType.STRING,
      // gender
      FieldType.STRING,
      // birthday
      FieldType.DATE,
      // creationDate
      FieldType.DATETIME,
      // locationIP
      FieldType.STRING,
      // browserUsed
      FieldType.STRING
    };

  public static final FieldType[] VERTEX_CLASS_ORGANISATION_FIELD_TYPES =
    new FieldType[]{
      // id
      FieldType.LONG,
      // type
      FieldType.STRING,
      // name
      FieldType.STRING,
      // url
      FieldType.STRING
    };

  public static final FieldType[] VERTEX_CLASS_POST_FIELD_TYPES =
    new FieldType[]{
      // id
      FieldType.LONG,
      // imageFile
      FieldType.STRING,
      // creationDate
      FieldType.DATETIME,
      // locationIP
      FieldType.STRING,
      // browserUsed
      FieldType.STRING,
      // language
      FieldType.STRING,
      // content
      FieldType.STRING,
      // length
      FieldType.INT
    };

  /**
   * Edge class field types
   */

  public static final FieldType[] EDGE_CLASS_KNOWS_FIELD_TYPES =
    new FieldType[]{
      // source id
      FieldType.LONG,
      // target id
      FieldType.LONG,
      // creationDate
      FieldType.DATETIME
    };

  public static final FieldType[] EDGE_CLASS_HAS_TYPE_FIELD_TYPES =
    new FieldType[]{
      // source id
      FieldType.LONG,
      // target id
      FieldType.LONG,
    };

  public static final FieldType[] EDGE_CLASS_IS_LOCATED_IN_FIELD_TYPES =
    new FieldType[]{
      // source id
      FieldType.LONG,
      // target id
      FieldType.LONG,
    };

  public static final FieldType[] EDGE_CLASS_HAS_INTEREST_FIELD_TYPES =
    new FieldType[]{
      // source id
      FieldType.LONG,
      // target id
      FieldType.LONG,
    };

  public static final FieldType[] EDGE_CLASS_REPLY_OF_FIELD_TYPES =
    new FieldType[]{
      // source id
      FieldType.LONG,
      // target id
      FieldType.LONG,
    };

  public static final FieldType[] EDGE_CLASS_STUDY_AT_FIELD_TYPES =
    new FieldType[]{
      // source id
      FieldType.LONG,
      // target id
      FieldType.LONG,
      // classYear
      FieldType.INT
    };

  public static final FieldType[] EDGE_CLASS_HAS_MODERATOR_FIELD_TYPES =
    new FieldType[]{
      // source id
      FieldType.LONG,
      // target id
      FieldType.LONG,
    };

  public static final FieldType[] EDGE_CLASS_HAS_MEMBER_FIELD_TYPES =
    new FieldType[]{
      // source id
      FieldType.LONG,
      // target id
      FieldType.LONG,
      // joinDate
      FieldType.DATETIME
    };

  public static final FieldType[] EDGE_CLASS_HAS_TAG_FIELD_TYPES =
    new FieldType[]{
      // source id
      FieldType.LONG,
      // target id
      FieldType.LONG,
    };

  public static final FieldType[] EDGE_CLASS_HAS_CREATOR_FIELD_TYPES =
    new FieldType[]{
      // source id
      FieldType.LONG,
      // target id
      FieldType.LONG,
    };

  public static final FieldType[] EDGE_CLASS_WORK_AT_FIELD_TYPES =
    new FieldType[]{
      // source id
      FieldType.LONG,
      // target id
      FieldType.LONG,
      // workFrom
      FieldType.INT
    };

  public static final FieldType[] EDGE_CLASS_CONTAINER_OF_FIELD_TYPES =
    new FieldType[]{
      // source id
      FieldType.LONG,
      // target id
      FieldType.LONG,
    };

  public static final FieldType[] EDGE_CLASS_IS_PART_OF_FIELD_TYPES =
    new FieldType[]{
      // source id
      FieldType.LONG,
      // target id
      FieldType.LONG,
    };

  public static final FieldType[] EDGE_CLASS_IS_SUBCLASS_OF_FIELD_TYPES =
    new FieldType[]{
      // source id
      FieldType.LONG,
      // target id
      FieldType.LONG,
    };

  public static final FieldType[] EDGE_CLASS_LIKES_FIELD_TYPES =
    new FieldType[]{
      // source id
      FieldType.LONG,
      // target id
      FieldType.LONG,
      // creationDate
      FieldType.DATETIME
    };

  /**
   * Property class field types
   */

  public static final FieldType[] PROPERTY_CLASS_EMAIL_FIELD_TYPES =
    new FieldType[]{
      // id
      FieldType.LONG,
      // email
      FieldType.STRING
    };

  public static final FieldType[] PROPERTY_CLASS_SPEAKS_FIELD_TYPES =
    new FieldType[]{
      // id
      FieldType.LONG,
      // language
      FieldType.STRING
    };
}
