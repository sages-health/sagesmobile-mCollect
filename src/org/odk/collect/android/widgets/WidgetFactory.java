/*
 * Copyright (C) 2009 University of Washington
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.odk.collect.android.widgets;

import java.util.Locale;

import org.javarosa.core.model.Constants;
import org.javarosa.form.api.FormEntryPrompt;
import org.odk.collect.android.widgets.QuestionWidget.OnAnswerChangedListener;

import android.content.Context;
import android.util.Log;

/**
 * Convenience class that handles creation of widgets.
 *
 * @author Carl Hartung (carlhartung@gmail.com)
 */
public class WidgetFactory {

    /**
     * Returns the appropriate QuestionWidget for the given FormEntryPrompt.
     *
     * @param fep prompt element to be rendered
     * @param context Android context
     */
    static public QuestionWidget createWidgetFromPrompt(FormEntryPrompt fep, Context context, OnAnswerChangedListener onAnswerChangedListener) {

    	// get appearance hint and clean it up so it is lower case and never null...
        String appearance = fep.getAppearanceHint();
        if ( appearance == null ) appearance = "";
        // for now, all appearance tags are in english...
        appearance = appearance.toLowerCase(Locale.ENGLISH);

        QuestionWidget questionWidget = null;
        switch (fep.getControlType()) {
            case Constants.CONTROL_INPUT:
                switch (fep.getDataType()) {
                    case Constants.DATATYPE_DATE_TIME:
                        questionWidget = new DateTimeWidget(context, fep, onAnswerChangedListener);
                        break;
                    case Constants.DATATYPE_DATE:
                        questionWidget = new DateWidget(context, fep, onAnswerChangedListener);
                        break;
                    case Constants.DATATYPE_TIME:
                        questionWidget = new TimeWidget(context, fep, onAnswerChangedListener);
                        break;
                    case Constants.DATATYPE_DECIMAL:
                    	if ( appearance.startsWith("ex:") ) {
                            questionWidget = new ExDecimalWidget(context, fep, onAnswerChangedListener);
                        } else if (appearance.equals("bearing")) {
                            questionWidget = new BearingWidget(context, fep, onAnswerChangedListener);
                        } else {
                            questionWidget = new DecimalWidget(context, fep, onAnswerChangedListener);
                        }
                        break;
                    case Constants.DATATYPE_INTEGER:
                    	if ( appearance.startsWith("ex:") ) {
                    		questionWidget = new ExIntegerWidget(context, fep, onAnswerChangedListener);
                    	} else {
                    		questionWidget = new IntegerWidget(context, fep, onAnswerChangedListener);
                    	}
                        break;
                    case Constants.DATATYPE_GEOPOINT:
                        questionWidget = new GeoPointWidget(context, fep, onAnswerChangedListener);
                        break;
                    case Constants.DATATYPE_BARCODE:
                        questionWidget = new BarcodeWidget(context, fep, onAnswerChangedListener);
                        break;
                    case Constants.DATATYPE_TEXT:
                    	String query = fep.getQuestion().getAdditionalAttribute(null, "query");
                        if (query != null) {
                            questionWidget = new ItemsetWidget(context, fep, onAnswerChangedListener);
                        } else if (appearance.startsWith("printer")) {
                            questionWidget = new ExPrinterWidget(context, fep, onAnswerChangedListener);
                        } else if (appearance.startsWith("ex:")) {
                            questionWidget = new ExStringWidget(context, fep, onAnswerChangedListener);
                        } else if (appearance.equals("numbers")) {
                            questionWidget = new StringNumberWidget(context, fep, onAnswerChangedListener);
                        } else if (appearance.equals("url")) {
                            questionWidget = new UrlWidget(context, fep, onAnswerChangedListener);
                        } else {
                            questionWidget = new StringWidget(context, fep, onAnswerChangedListener);
                        }
                        break;
                    default:
                        questionWidget = new StringWidget(context, fep, onAnswerChangedListener);
                        break;
                }
                break;
            case Constants.CONTROL_IMAGE_CHOOSE:
            	if (appearance.equals("web")) {
            		questionWidget = new ImageWebViewWidget(context, fep, onAnswerChangedListener);
        		} else if(appearance.equals("signature")) {
            		questionWidget = new SignatureWidget(context, fep, onAnswerChangedListener);
            	} else if(appearance.equals("annotate")) {
            		questionWidget = new AnnotateWidget(context, fep, onAnswerChangedListener);
            	} else if(appearance.equals("draw")) {
            		questionWidget = new DrawWidget(context, fep, onAnswerChangedListener);
            	} else if(appearance.startsWith("align:")) {
            		questionWidget = new AlignedImageWidget(context, fep, onAnswerChangedListener);
            	} else {
            		questionWidget = new ImageWidget(context, fep, onAnswerChangedListener);
            	}
                break;
            case Constants.CONTROL_AUDIO_CAPTURE:
                questionWidget = new AudioWidget(context, fep, onAnswerChangedListener);
                break;
            case Constants.CONTROL_VIDEO_CAPTURE:
                questionWidget = new VideoWidget(context, fep, onAnswerChangedListener);
                break;
            case Constants.CONTROL_SELECT_ONE:
                if (appearance.contains("compact")) {
                    int numColumns = -1;
                    try {
                    	int idx = appearance.indexOf("-");
                    	if ( idx != -1 ) {
                    		numColumns =
                    				Integer.parseInt(appearance.substring(idx + 1));
                    	}
                    } catch (Exception e) {
                        // Do nothing, leave numColumns as -1
                        Log.e("WidgetFactory", "Exception parsing numColumns");
                    }

                    if (appearance.contains("quick")) {
                        questionWidget = new GridWidget(context, fep, numColumns, true, onAnswerChangedListener);
                    } else {
                        questionWidget = new GridWidget(context, fep, numColumns, false, onAnswerChangedListener);
                    }
                } else if (appearance.equals("minimal")) {
                    questionWidget = new SpinnerWidget(context, fep, onAnswerChangedListener);
                }
                // else if (appearance != null && appearance.contains("autocomplete")) {
                // String filterType = null;
                // try {
                // filterType = appearance.substring(appearance.indexOf('-') + 1);
                // } catch (Exception e) {
                // // Do nothing, leave filerType null
                // Log.e("WidgetFactory", "Exception parsing filterType");
                // }
                // questionWidget = new AutoCompleteWidget(context, fep, filterType);
                //
                // }
                else if (appearance.equals("quick")) {
                    questionWidget = new SelectOneAutoAdvanceWidget(context, fep, onAnswerChangedListener);
                } else if (appearance.equals("list-nolabel")) {
                    questionWidget = new ListWidget(context, fep, false, onAnswerChangedListener);
                } else if (appearance.equals("list")) {
                    questionWidget = new ListWidget(context, fep, true, onAnswerChangedListener);
                } else if (appearance.equals("label")) {
                    questionWidget = new LabelWidget(context, fep);
                } else {
                    questionWidget = new SelectOneWidget(context, fep, onAnswerChangedListener);
                }
                break;
            case Constants.CONTROL_SELECT_MULTI:
                if (appearance.contains("compact")) {
                    int numColumns = -1;
                    try {
                    	int idx = appearance.indexOf("-");
                    	if ( idx != -1 ) {
                    		numColumns =
                    				Integer.parseInt(appearance.substring(idx + 1));
                    	}
                    } catch (Exception e) {
                        // Do nothing, leave numColumns as -1
                        Log.e("WidgetFactory", "Exception parsing numColumns");
                    }

                    questionWidget = new GridMultiWidget(context, fep, numColumns, onAnswerChangedListener);
                } else if (appearance.equals("minimal")) {
                    questionWidget = new SpinnerMultiWidget(context, fep, onAnswerChangedListener);
                } else if (appearance.equals("list")) {
                    questionWidget = new ListMultiWidget(context, fep, true, onAnswerChangedListener);
                } else if (appearance.equals("list-nolabel")) {
                    questionWidget = new ListMultiWidget(context, fep, false, onAnswerChangedListener);
                } else if (appearance.equals("label")) {
                    questionWidget = new LabelWidget(context, fep);
                } else {
                    questionWidget = new SelectMultiWidget(context, fep, onAnswerChangedListener);
                }
                break;
            case Constants.CONTROL_TRIGGER:
                questionWidget = new TriggerWidget(context, fep, onAnswerChangedListener);
                break;
            default:
                questionWidget = new StringWidget(context, fep, onAnswerChangedListener);
                break;
        }
        return questionWidget;
    }

}
